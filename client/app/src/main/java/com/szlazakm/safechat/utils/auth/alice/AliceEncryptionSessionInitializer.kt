package com.szlazakm.safechat.utils.auth.alice

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.ecc.RootKey
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.DiffieHellman
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

    suspend fun getInitialMessageEncryptionBundle(phoneNumber: String): InitialMessageEncryptionBundle {

        return withContext(Dispatchers.IO) {

            val keyBundleDTO = getKeyBundleForUser(phoneNumber)
                ?: throw NotFoundException("Failed to retrieve keyBundle for phoneNumber: $phoneNumber")

            if(!EccKeyHelper.verifySignature(keyBundleDTO)) {
                throw IllegalArgumentException("Signature verification failed (signed preKey signature is incorrect)")
            }

            val localUser = userRepository.getLocalUser()

            val initializationKeyBundle = InitializationKeyBundle.initializeKeyBundle(keyBundleDTO, localUser)

            val masterSecret = SymmetricKeyGenerator.generateSymmetricKey(initializationKeyBundle)
            Log.d("SafeChat:AliceEncryptionSessionInitializer", "master secret: ${masterSecret.toHex()}")

            val rootKey = RootKey(masterSecret)
            val ephemeralKeyPair = initializationKeyBundle.ratchetEphemeralKeyPair
            val signedPreKey = Decoder.decode(keyBundleDTO.signedPreKey)

            val ratchetSendingChain = rootKey.createChain(
                theirRatchetPublicKey = signedPreKey,
                ourRatchetPrivateKey = ephemeralKeyPair.privateKey
            )

            InitialMessageEncryptionBundle.initializeInitialMessageEncryptionBundle(
                keyBundleDTO = keyBundleDTO,
                localUser = localUser,
                initializationKeyBundle = initializationKeyBundle,
                ratchetSendingChain = ratchetSendingChain
            )
        }
    }

    private fun getKeyBundleForUser(phoneNumber: String) : KeyBundleDTO? {

        try {

            val response = userWebService.getKeyBundle(phoneNumber).execute()

            if (response.isSuccessful) {

                Log.d("EncryptionSessionInitializer", "Successfully received key bundle.")
                return response.body()
            } else {
                Log.e("EncryptionSessionInitializer", "Failed to receive key bundle.")
            }

        } catch (e: Exception) {
            Log.e(
                "EncryptionSessionInitializer",
                "Exception while trying to receive key bundle. ${e.message}"
            )
        }

        return null
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}