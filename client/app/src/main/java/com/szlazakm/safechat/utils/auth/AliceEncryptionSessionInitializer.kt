package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.DiffieHellman
import com.szlazakm.safechat.utils.auth.utils.KDF
import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO
import com.szlazakm.safechat.webclient.webservices.UserWebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AliceEncryptionSessionInitializer @Inject constructor(
    private val userWebService: UserWebService,
    private val userRepository: UserRepository
){
    private val diffieHellman = DiffieHellman()

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

            val signingKey = Decoder.decode(keyBundleDTO.identityKey)
            val message = Decoder.decode(keyBundleDTO.signedPreKey)
            val signature = Decoder.decode(keyBundleDTO.signature)

            if(!verifySignature(signingKey, message, signature)) {
                Log.e("EncryptionSessionManager", "Signature verification failed.")
                return@withContext null
            }

            val user = userRepository.getLocalUser()

            val initializationKeyBundle = initializeKeyBundle(keyBundleDTO, user)

            val symmetricKey = generateSymmetricKey(initializationKeyBundle)
            val derivedKeys = KDF.calculateDerivedKeys(symmetricKey)

            initializeInitialMessageEncryptionBundle(
                keyBundleDTO,
                user,
                initializationKeyBundle,
                derivedKeys.rootKey.key
            )
        }
    }

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

    private fun initializeKeyBundle(keyBundleDTO: KeyBundleDTO, localUser: UserEntity): InitializationKeyBundle {
        val bobIdentityKey = keyBundleDTO.identityKey
        val bobSignedPreKey = keyBundleDTO.signedPreKey
        var bobOpk : ByteArray? = null

        if(keyBundleDTO.onetimePreKey != null) {
            Log.d("EncryptionSessionManager", "Bob's OPK is null.")
            bobOpk = Decoder.decode(keyBundleDTO.onetimePreKey)
        }

        val aliceEphemeralKeyPair = EccKeyHelper.generateSenderKeyPair()
        val aliceEphemeralPrivateKey = aliceEphemeralKeyPair.privateKey
        val aliceEphemeralPublicKey = aliceEphemeralKeyPair.publicKey
        val alicePrivateIdentityKey = Decoder.decode(localUser.privateIdentityKey)

        return InitializationKeyBundle(
            Decoder.decode(bobIdentityKey),
            Decoder.decode(bobSignedPreKey),
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

        val dh1 = diffieHellman.createSharedSecret(alicePrivateIdentityKey, bobSignedPreKey)
        val dh2 = diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobIdentityKey)
        val dh3 = diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobSignedPreKey)

        val dh4 = if (bobOpk != null) diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobOpk) else byteArrayOf()

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
        symmetricKey: ByteArray
    ): InitialMessageEncryptionBundle {

        val alicePublicIdentityKey = Decoder.decode(localUser.publicIdentityKey)
        val ad = alicePublicIdentityKey + Decoder.decode(keyBundleDTO.identityKey)

        return InitialMessageEncryptionBundle(
            alicePublicIdentityKey,
            initializationKeyBundle.aliceEphemeralPublicKey,
            keyBundleDTO.onetimePreKeyId,
            keyBundleDTO.signedPreKeyId,
            symmetricKey,
            ad
        )

    }

    private fun verifySignature(signingKey: ByteArray, message: ByteArray, signature: ByteArray) : Boolean{

        return EccKeyHelper.verifySignature(signingKey, message, signature)
    }

    class InitialMessageEncryptionBundle(
        val aliceIdentityKey: ByteArray,
        val aliceEphemeralPublicKey: ByteArray,
        val bobOpkId: Int?,
        val bobSignedPreKeyId: Int,
        val symmetricKey: ByteArray,
        val ad: ByteArray
    )

}