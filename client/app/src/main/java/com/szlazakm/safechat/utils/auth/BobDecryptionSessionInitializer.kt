package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
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

            val ad = initializationKeyBundle.aliceIdentityKey + initializationKeyBundle.bobPublicIdentityKey

            BobInitializeSessionBundle(
                symmetricKey,
                ad
            )
        }
    }

    private fun generateSymmetricKeyWithOPK(
        aliceIdentityKey: ByteArray,
        aliceEphemeralKey: ByteArray,
        bobOpk: ByteArray,
        bobSpk: ByteArray,
        bobPrivateIdentityKey: ByteArray
    ) : ByteArray{

        val dh1 = diffieHellman.createSharedSecret(bobSpk, aliceIdentityKey)
        val dh2 = diffieHellman.createSharedSecret(bobPrivateIdentityKey, aliceEphemeralKey)
        val dh3 = diffieHellman.createSharedSecret(bobSpk, aliceEphemeralKey)
        val dh4 = diffieHellman.createSharedSecret(bobOpk, aliceEphemeralKey)

        Log.d("BobDecryptionSessionInitializer", "DH1: ${Base64.getEncoder().encodeToString(dh1)}")
        Log.d("BobDecryptionSessionInitializer", "DH2: ${Base64.getEncoder().encodeToString(dh2)}")
        Log.d("BobDecryptionSessionInitializer", "DH3: ${Base64.getEncoder().encodeToString(dh3)}")
        Log.d("BobDecryptionSessionInitializer", "DH4: ${Base64.getEncoder().encodeToString(dh4)}")

        return dh1 + dh2 + dh3 + dh4
    }

    private fun generateSymmetricKeyWithoutOPK(
        aliceIdentityKey: ByteArray,
        aliceEphermalKey: ByteArray,
        bobSpk: ByteArray,
        bobPrivateIdentityKey: ByteArray
    ) : ByteArray{

        val dh1 = diffieHellman.createSharedSecret(bobSpk, aliceIdentityKey)
        val dh2 = diffieHellman.createSharedSecret(bobPrivateIdentityKey, aliceEphermalKey)
        val dh3 = diffieHellman.createSharedSecret(bobSpk, aliceEphermalKey)
        return dh1 + dh2 + dh3
    }

    private suspend fun initializeKeyBundle(encryptedMessage: OutputEncryptedMessageDTO, localUser: UserEntity): BobInitializationKeyBundle? {
        val aliceIdentityKey = encryptedMessage.aliceIdentityPublicKey
        val aliceEphemeralPublicKey = encryptedMessage.aliceEphemeralPublicKey

        if(encryptedMessage.bobOpkId == null) {
            Log.e("BobDecryptionSessionInitializer", "Bob OPK ID is null.")
            return null
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

        val bobOpk = (Dispatchers.IO) {
            preKeyRepository.getOPKById(encryptedMessage.bobOpkId)
        }.privateOPK
        val bobSignedPreKey = (Dispatchers.IO) {
            preKeyRepository.getSPKById(encryptedMessage.bobSpkId)
        }.privateKey

        val normalizedIdentityKeyPair = decode(localUser.identityKeyPair)
        val identityKeyPair = IdentityKeyPair(normalizedIdentityKeyPair)
        val bobPrivateIdentityKey = (identityKeyPair.privateKey as DjbECPrivateKey).privateKey
        val bobPublicIdentityKey = (identityKeyPair.publicKey.publicKey as DjbECPublicKey).publicKey

        return BobInitializationKeyBundle(
            decode(aliceIdentityKey),
            decode(aliceEphemeralPublicKey),
            decode(bobOpk),
            decode(bobSignedPreKey),
            bobPrivateIdentityKey,
            bobPublicIdentityKey
        )
    }

    private fun generateSymmetricKey(initializationKeyBundle: BobInitializationKeyBundle) : ByteArray {

        val symmetricKey: ByteArray

        if(initializationKeyBundle.bobOpk != null) {
            symmetricKey = generateSymmetricKeyWithOPK(
                initializationKeyBundle.aliceIdentityKey,
                initializationKeyBundle.aliceEphermalKey,
                initializationKeyBundle.bobOpk,
                initializationKeyBundle.bobSpk,
                initializationKeyBundle.bobPrivateIdentityKey
            )
        } else {
            symmetricKey = generateSymmetricKeyWithoutOPK(
                initializationKeyBundle.aliceIdentityKey,
                initializationKeyBundle.aliceEphermalKey,
                initializationKeyBundle.bobSpk,
                initializationKeyBundle.bobPrivateIdentityKey
            )
        }

        return symmetricKey
    }

    class BobInitializationKeyBundle(
        val aliceIdentityKey: ByteArray,
        val aliceEphermalKey: ByteArray,
        val bobOpk: ByteArray? = null,
        val bobSpk: ByteArray,
        val bobPrivateIdentityKey: ByteArray,
        val bobPublicIdentityKey: ByteArray
    )

    class BobInitializeSessionBundle(
        val symmetricKey: ByteArray,
        val ad: ByteArray
    )

    private fun encode(byteArray: ByteArray): String {
        return Base64.getEncoder().encodeToString(byteArray)
    }

    private fun decode(string: String): ByteArray {
        return Base64.getDecoder().decode(string)
    }
}