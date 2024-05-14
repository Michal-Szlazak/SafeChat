package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.domain.LocalUserData
import com.szlazakm.safechat.webclient.dtos.OutputEncryptedMessageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.withContext
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.ecc.DjbECPrivateKey
import org.whispersystems.libsignal.ecc.DjbECPublicKey
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BobDecryptionSessionInitializer @Inject constructor(
    private val userRepository: UserRepository,
    private val preKeyRepository: PreKeyRepository
) {

    private val diffieHellman = DiffieHellman()

    suspend fun createSymmetricKey(encryptedMessage: OutputEncryptedMessageDTO) : BobInitializeSessionBundle? {

        return withContext(Dispatchers.IO) {

            val localUser = userRepository.getLocalUser()

            if(localUser == null) {
                Log.e("EncryptionSessionManager", "Failed to get local user.")
                return@withContext null
            }

            val initializationKeyBundle = initializeKeyBundle(encryptedMessage, localUser)

            if(initializationKeyBundle == null) {
                Log.e("BobDecryptionSessionInitializer", "Failed to initialize key bundle.")
                return@withContext null
            }

            val symmetricKey = generateSymmetricKey(initializationKeyBundle)
            val derivedKeys = KDF.calculateDerivedKeys(symmetricKey)

            val ad = initializationKeyBundle.aliceIdentityKey + initializationKeyBundle.bobPublicIdentityKey

            BobInitializeSessionBundle(
                derivedKeys.rootKey.keyBytes,
                ad
            )
        }
    }

    private suspend fun initializeKeyBundle(encryptedMessage: OutputEncryptedMessageDTO, localUser: UserEntity): BobInitializationKeyBundle? {
        val aliceIdentityKey = encryptedMessage.aliceIdentityPublicKey
        val aliceEphemeralPublicKey = encryptedMessage.aliceEphemeralPublicKey
        var bobOpk : ByteArray? = null

        if(encryptedMessage.bobOpkId != null) {

            val encodedBobOpk = (Dispatchers.IO) {
                preKeyRepository.getOPKById(encryptedMessage.bobOpkId)
            }.privateOPK

            bobOpk = decode(encodedBobOpk)
        }

        if(encryptedMessage.bobSpkId == null) {
            Log.e("BobDecryptionSessionInitializer", "Bob SPK ID is null.")
            return null
        }

        if(aliceIdentityKey == null) {
            Log.e("BobDecryptionSessionInitializer", "Alice identity key is null.")
            return null
        }

        if(aliceEphemeralPublicKey == null) {
            Log.e("BobDecryptionSessionInitializer", "Alice ephemeral public key is null.")
            return null
        }

        val bobSignedPreKey = (Dispatchers.IO) {
            preKeyRepository.getSPKById(encryptedMessage.bobSpkId)
        }.privateKey

        val bobPrivateIdentityKey = decode(localUser.privateIdentityKey)
        val bobPublicIdentityKey = decode(localUser.publicIdentityKey)

        return BobInitializationKeyBundle(
            decode(aliceIdentityKey),
            decode(aliceEphemeralPublicKey),
            bobOpk,
            decode(bobSignedPreKey),
            bobPrivateIdentityKey,
            bobPublicIdentityKey
        )
    }

    private fun generateSymmetricKey(initializationKeyBundle: BobInitializationKeyBundle) : ByteArray {

        val aliceIdentityKey = initializationKeyBundle.aliceIdentityKey
        val aliceEphemeralKey = initializationKeyBundle.aliceEphemeralKey
        val bobOpk = initializationKeyBundle.bobOpk
        val bobSpk = initializationKeyBundle.bobSpk
        val bobPrivateIdentityKey = initializationKeyBundle.bobPrivateIdentityKey

        val dh1 = diffieHellman.createSharedSecret(bobSpk, aliceIdentityKey)
        val dh2 = diffieHellman.createSharedSecret(bobPrivateIdentityKey, aliceEphemeralKey)
        val dh3 = diffieHellman.createSharedSecret(bobSpk, aliceEphemeralKey)
        val dh4 = if (bobOpk != null) diffieHellman.createSharedSecret(bobOpk, aliceEphemeralKey) else byteArrayOf()

        return dh1 + dh2 + dh3 + dh4

    }

    class BobInitializationKeyBundle(
        val aliceIdentityKey: ByteArray,
        val aliceEphemeralKey: ByteArray,
        val bobOpk: ByteArray? = null,
        val bobSpk: ByteArray,
        val bobPrivateIdentityKey: ByteArray,
        val bobPublicIdentityKey: ByteArray
    )

    class BobInitializeSessionBundle(
        val symmetricKey: ByteArray,
        val ad: ByteArray
    )

    private fun decode(string: String): ByteArray {
        return Base64.getDecoder().decode(string)
    }
}