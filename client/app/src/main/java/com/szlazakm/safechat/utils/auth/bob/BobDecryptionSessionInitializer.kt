package com.szlazakm.safechat.utils.auth.bob

import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.ecc.ReceiverChainKey
import com.szlazakm.safechat.utils.auth.ecc.RootKey
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.DiffieHellman
import com.szlazakm.safechat.utils.auth.utils.Encoder
import com.szlazakm.safechat.webclient.dtos.OutputEncryptedMessageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BobDecryptionSessionInitializer @Inject constructor(
    private val userRepository: UserRepository,
    private val preKeyRepository: PreKeyRepository
) {

    suspend fun getInitialMessageEncryptionBundle(encryptedMessage: OutputEncryptedMessageDTO) : InitialMessageDecryptionBundle? {

        return withContext(Dispatchers.IO) {

            val localUser = userRepository.getLocalUser()

            val initializationKeyBundle = initializeKeyBundle(encryptedMessage, localUser)

            if(initializationKeyBundle == null) {
                Log.e("BobDecryptionSessionInitializer", "Failed to initialize key bundle.")
                return@withContext null
            }

            val masterSecret = SymmetricKeyGenerator.generateSymmetricKey(initializationKeyBundle)
            Log.d("SafeChat:BobDecryptionSessionInitializer", "master secret: ${masterSecret.toHex()}")

            if(encryptedMessage.bobSpkId == null) {
                Log.e("EncryptionSessionManager", "Initial message doesn't have bob spk id - which is required.")
                return@withContext null
            }

            var rootKey = RootKey(masterSecret)
            val ephemeralRatchetPublicKey = encryptedMessage.ephemeralRatchetKey
            val signedPreKey = (Dispatchers.IO) {
                preKeyRepository.getSPKById(encryptedMessage.bobSpkId)
            }.privateKey

            val receiverKeyPair = rootKey.createChain(
                ourRatchetPrivateKey = Decoder.decode(signedPreKey),
                theirRatchetPublicKey = Decoder.decode(ephemeralRatchetPublicKey),
            )

            rootKey = receiverKeyPair.first

            val receiverChainKey = ReceiverChainKey(
                key = receiverKeyPair.second.key,
                index = 0,
                publicEphemeralKey = Decoder.decode(encryptedMessage.ephemeralRatchetKey)
            )

            val sendingKeyPair = rootKey.createChain(
                ourRatchetPrivateKey = initializationKeyBundle.bobEphemeralRatchetEccKeyPair.privateKey,
                theirRatchetPublicKey = Decoder.decode(ephemeralRatchetPublicKey)
            )

            InitialMessageDecryptionBundle(
                aliceEphemeralRatchetEccPublicKey = Decoder.decode(signedPreKey),
                ratchetKeyPair = sendingKeyPair,
                receiverChainKey = receiverChainKey,
                ourIdentityPublicKey = Decoder.decode(localUser.publicIdentityKey),
                theirIdentityPublicKey = initializationKeyBundle.aliceIdentityKey,
                bobEphemeralRatchetEccKeyPair = initializationKeyBundle.bobEphemeralRatchetEccKeyPair
            )
        }
    }

    private suspend fun initializeKeyBundle(encryptedMessage: OutputEncryptedMessageDTO, localUser: UserEntity): InitializationKeyBundle? {
        val aliceIdentityKey = encryptedMessage.aliceIdentityPublicKey
        val aliceEphemeralPublicKey = encryptedMessage.aliceEphemeralPublicKey
        var bobOpk: ByteArray? = null

        if (encryptedMessage.bobOpkId != null) {

            val encodedBobOpk = (Dispatchers.IO) {
                preKeyRepository.getOPKById(encryptedMessage.bobOpkId)
            }.privateOPK

            bobOpk = Decoder.decode(encodedBobOpk)
        }

        if (encryptedMessage.bobSpkId == null) {
            Log.e("BobDecryptionSessionInitializer", "Bob SPK ID is null.")
            return null
        }

        if (aliceIdentityKey == null) {
            Log.e("BobDecryptionSessionInitializer", "Alice identity key is null.")
            return null
        }

        if (aliceEphemeralPublicKey == null) {
            Log.e("BobDecryptionSessionInitializer", "Alice ephemeral public key is null.")
            return null
        }

        val bobSignedPreKey = (Dispatchers.IO) {
            preKeyRepository.getSPKById(encryptedMessage.bobSpkId)
        }.privateKey

        val bobPrivateIdentityKey = Decoder.decode(localUser.privateIdentityKey)

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())

        val ratchetKeyPair = EccKeyHelper.generateKeyPair()

        return InitializationKeyBundle(
            Decoder.decode(aliceIdentityKey),
            Decoder.decode(aliceEphemeralPublicKey),
            bobOpk,
            Decoder.decode(bobSignedPreKey),
            bobPrivateIdentityKey,
            ratchetKeyPair
        )
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}