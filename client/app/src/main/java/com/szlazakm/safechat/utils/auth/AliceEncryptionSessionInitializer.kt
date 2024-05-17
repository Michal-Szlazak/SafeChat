package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.rachet.ChainKey
import com.szlazakm.safechat.utils.auth.rachet.KeyPair
import com.szlazakm.safechat.utils.auth.rachet.RatchetSession
import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO
import com.szlazakm.safechat.webclient.webservices.UserWebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.whispersystems.curve25519.Curve25519
import org.whispersystems.curve25519.Curve25519.BEST
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AliceEncryptionSessionInitializer @Inject constructor(
    private val userWebService: UserWebService,
    private val userRepository: UserRepository
){

    private fun getKeyBundleForUser(phoneNumber: String) : KeyBundleDTO? {

        try {

            val response = userWebService.getKeyBundle(phoneNumber).execute()

            if(response.isSuccessful) {

                Log.d("EncryptionSessionInitializer", "Successfully received key bundle.")
                return response.body()
            } else {
                Log.e("EncryptionSessionInitializer", "Failed to receive key bundle.")
            }

        } catch (e: Exception) {
            Log.e("EncryptionSessionInitializer",
                "Exception while trying to receive key bundle. ${e.message}")
        }

        return null
    }

    suspend fun getInitialMessageEncryptionBundle(phoneNumber: String): InitialMessageEncryptionBundle? {

        return withContext(Dispatchers.IO) {
            val keyBundleDTO = getKeyBundleForUser(phoneNumber)

            if (keyBundleDTO == null) {
                Log.e(
                    "EncryptionSessionManager",
                    "Failed to get key bundle for phone: $phoneNumber"
                )
                return@withContext null
            }

            val signingKey = decode(keyBundleDTO.identityKey)
            val message = decode(keyBundleDTO.signedPreKey)
            val signature = decode(keyBundleDTO.signature)

            if(!verifySignature(signingKey, message, signature)) {
                Log.e("EncryptionSessionManager", "Signature verification failed.")
                return@withContext null
            }

            val user = userRepository.getLocalUser()
            if (user == null) {
                Log.e("EncryptionSessionManager", "Failed to get local user.")
                return@withContext null
            }

            val initializationKeyBundle = initializeKeyBundle(keyBundleDTO, user)
            val ratchetSession = initializeRatchetSession(initializationKeyBundle)

            //TODO implement double ratchet
            val symmetricKey = generateSymmetricKey(initializationKeyBundle)

            val keyPair = KDF.calculateDerivedKeys(symmetricKey)

            initializeInitialMessageEncryptionBundle(
                keyBundleDTO,
                user,
                initializationKeyBundle,
                keyPair.rootKey.key,
                ratchetSession
            )
        }
    }

    private fun initializeRatchetSession(initializationKeyBundle: InitializationKeyBundle) : RatchetSession{

        val symmetricKey = generateSymmetricKey(initializationKeyBundle)

        val keyPair = KDF.calculateDerivedKeys(symmetricKey)

        val theirRatchetKey = ChainKey(initializationKeyBundle.bobSignedPreKey, 0)
        val ourRatchetKeyPair = EccKeyHelper.generateKeyPair()
        val ourRatchetPublicKey = ChainKey(ourRatchetKeyPair.privateKey, 0)
        val ourRatchetPrivateKey = ChainKey(ourRatchetKeyPair.publicKey, 0)

        val sendingChain: KeyPair = keyPair.rootKey.createChain(
            theirRatchetKey,
            ourRatchetPrivateKey
        )

        val receiverChainKey = keyPair.chainKey

        return RatchetSession(
            sendingChain.rootKey,
            sendingChain.chainKey,
            ourRatchetPrivateKey,
            ourRatchetPublicKey,
            theirRatchetKey,
            receiverChainKey
        )
    }

    private fun initializeKeyBundle(keyBundleDTO: KeyBundleDTO, localUser: UserEntity): InitializationKeyBundle {
        val bobIdentityKey = keyBundleDTO.identityKey
        val bobSignedPreKey = keyBundleDTO.signedPreKey
        var bobOpk : ByteArray? = null

        if(keyBundleDTO.onetimePreKey != null) {
            Log.d("EncryptionSessionManager", "Bob's OPK is null.")
            bobOpk = decode(keyBundleDTO.onetimePreKey)
        }

        val aliceEphemeralKeyPair = EccKeyHelper.generateSenderKeyPair()
        val aliceEphemeralPrivateKey = aliceEphemeralKeyPair.privateKey
        val aliceEphemeralPublicKey = aliceEphemeralKeyPair.publicKey
        val alicePrivateIdentityKey = decode(localUser.privateIdentityKey)

        return InitializationKeyBundle(
            decode(bobIdentityKey),
            decode(bobSignedPreKey),
            bobOpk,
            alicePrivateIdentityKey,
            aliceEphemeralPrivateKey,
            aliceEphemeralPublicKey
        )
    }

    private fun generateSymmetricKey(initializationKeyBundle: InitializationKeyBundle) : ByteArray {

        val bobIdentityKey = initializationKeyBundle.bobIdentityKey
        val bobSignedPreKey = initializationKeyBundle.bobSignedPreKey
        val bobOpk = initializationKeyBundle.bobOpk
        val alicePrivateIdentityKey = initializationKeyBundle.alicePrivateIdentityKey
        val aliceEphemeralPrivateKey = initializationKeyBundle.aliceEphemeralPrivateKey

        val dh1 = DiffieHellman.createSharedSecret(alicePrivateIdentityKey, bobSignedPreKey)
        val dh2 = DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobIdentityKey)
        val dh3 = DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobSignedPreKey)

        val dh4 = if (bobOpk != null) DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobOpk) else byteArrayOf()

        return dh1 + dh2 + dh3 + dh4
    }

    class InitializationKeyBundle(
        val bobIdentityKey: ByteArray,
        val bobSignedPreKey: ByteArray,
        val bobOpk: ByteArray? = null,
        val alicePrivateIdentityKey: ByteArray,
        val aliceEphemeralPrivateKey: ByteArray,
        val aliceEphemeralPublicKey: ByteArray
    )

    private fun initializeInitialMessageEncryptionBundle(
        keyBundleDTO: KeyBundleDTO,
        localUser: UserEntity,
        initializationKeyBundle: InitializationKeyBundle,
        symmetricKey: ByteArray,
        ratchetSession: RatchetSession
    ): InitialMessageEncryptionBundle {

        val alicePublicIdentityKey = decode(localUser.publicIdentityKey)
        val ad = alicePublicIdentityKey + decode(keyBundleDTO.identityKey)

        return InitialMessageEncryptionBundle(
            alicePublicIdentityKey,
            initializationKeyBundle.aliceEphemeralPublicKey,
            keyBundleDTO.onetimePreKeyId,
            keyBundleDTO.signedPreKeyId,
            symmetricKey,
            ad,
            ratchetSession
        )

    }

    private fun verifySignature(signingKey: ByteArray, message: ByteArray, signature: ByteArray) : Boolean{

        return Curve25519.getInstance(BEST).verifySignature(signingKey, message, signature)
    }

    class InitialMessageEncryptionBundle(
        val aliceIdentityKey: ByteArray,
        val aliceEphemeralPublicKey: ByteArray,
        val bobOpkId: Int?,
        val bobSignedPreKeyId: Int,
        val symmetricKey: ByteArray,
        val ad: ByteArray,
        val ratchetSession: RatchetSession
    )

    private fun decode(string: String): ByteArray {
        return Base64.getDecoder().decode(string)
    }
}