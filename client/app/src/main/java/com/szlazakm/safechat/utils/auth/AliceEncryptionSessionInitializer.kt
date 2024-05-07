package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO
import com.szlazakm.safechat.webclient.webservices.UserWebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.ecc.DjbECPrivateKey
import org.whispersystems.libsignal.ecc.DjbECPublicKey
import org.whispersystems.libsignal.util.KeyHelper
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AliceEncryptionSessionInitializer @Inject constructor(
    private val userWebService: UserWebService,
    private val userRepository: UserRepository
){

    private val diffieHellman = DiffieHellman()

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
                Log.e("EncryptionSessionManager", "Failed to get key bundle for phone: $phoneNumber")
                return@withContext null
            }

            //TODO verify the signature
            val user = userRepository.getLocalUser()
            if (user == null) {
                Log.e("EncryptionSessionManager", "Failed to get local user.")
                return@withContext null
            }

            val initializationKeyBundle = initializeKeyBundle(keyBundleDTO, user)

            if(initializationKeyBundle == null) {
                Log.e("EncryptionSessionManager", "Failed to initialize key bundle.")
                return@withContext null
            }

            val symmetricKey = generateSymmetricKey(initializationKeyBundle)
            val derivedKeys = KDF.calculateDerivedKeys(symmetricKey)

            initializeInitialMessageEncryptionBundle(
                keyBundleDTO,
                user,
                initializationKeyBundle,
                derivedKeys.rootKey.keyBytes
            )
        }
    }

    private fun generateSymmetricKeyWithOPK(
        bobIdentityKey: ByteArray,
        bobSignedPreKey: ByteArray,
        bobOpk: ByteArray,
        alicePrivateIdentityKey: ByteArray,
        aliceEphemeralPrivateKey: ByteArray
    ) : ByteArray{

        val dh1 = diffieHellman.createSharedSecret(alicePrivateIdentityKey, bobSignedPreKey)
        val dh2 = diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobIdentityKey)
        val dh3 = diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobSignedPreKey)
        val dh4 = diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobOpk)

        return dh1 + dh2 + dh3 + dh4
    }

    private fun generateSymmetricKeyWithoutOPK(
        bobIdentityKey: ByteArray,
        bobSignedPreKey: ByteArray,
        alicePrivateIdentityKey: ByteArray,
        aliceEphemeralPrivateKey: ByteArray
    ) : ByteArray{

        val dh1 = diffieHellman.createSharedSecret(alicePrivateIdentityKey, bobSignedPreKey)
        val dh2 = diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobIdentityKey)
        val dh3 = diffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobSignedPreKey)
        return dh1 + dh2 + dh3
    }

    private fun initializeKeyBundle(keyBundleDTO: KeyBundleDTO, localUser: UserEntity): InitializationKeyBundle? {
        val bobIdentityKey = keyBundleDTO.identityKey
        val bobSignedPreKey = keyBundleDTO.signedPreKey
        var bobOpk : ByteArray? = null

        if(keyBundleDTO.onetimePreKey != null) {
            Log.d("EncryptionSessionManager", "Bob's OPK is null.")
            bobOpk = decode(keyBundleDTO.onetimePreKey)
        }

        val aliceEphemeralKeyPair = KeyHelper.generateSenderSigningKey()
        val aliceEphemeralPrivateKey = (aliceEphemeralKeyPair.privateKey as DjbECPrivateKey).privateKey
        val aliceEphemeralPublicKey = (aliceEphemeralKeyPair.publicKey as DjbECPublicKey).publicKey

        val normalizedIdentityKeyPair = decode(localUser.identityKeyPair)
        val identityKeyPair = IdentityKeyPair(normalizedIdentityKeyPair)
        val alicePrivateIdentityKey = (identityKeyPair.privateKey as DjbECPrivateKey).privateKey

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

        val symmetricKey: ByteArray

        if(initializationKeyBundle.bobOpk != null) {
            symmetricKey = generateSymmetricKeyWithOPK(
                initializationKeyBundle.bobIdentityKey,
                initializationKeyBundle.bobSignedPreKey,
                initializationKeyBundle.bobOpk,
                initializationKeyBundle.alicePrivateIdentityKey,
                initializationKeyBundle.aliceEphemeralPrivateKey
            )
        } else {
            symmetricKey = generateSymmetricKeyWithoutOPK(
                initializationKeyBundle.bobIdentityKey,
                initializationKeyBundle.bobSignedPreKey,
                initializationKeyBundle.alicePrivateIdentityKey,
                initializationKeyBundle.aliceEphemeralPrivateKey
            )
        }

        return symmetricKey
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

        val normalizedIdentityKeyPair = decode(localUser.identityKeyPair)
        val identityKeyPair = IdentityKeyPair(normalizedIdentityKeyPair)
        val alicePublicIdentityKey = (identityKeyPair.publicKey.publicKey as DjbECPublicKey).publicKey
        val ad = alicePublicIdentityKey + decode(keyBundleDTO.identityKey)

        return InitialMessageEncryptionBundle(
            alicePublicIdentityKey,
            initializationKeyBundle.aliceEphemeralPublicKey,
            keyBundleDTO.onetimePreKeyId,
            keyBundleDTO.signedPreKeyId,
            symmetricKey,
            ad
        )

    }

    class InitialMessageEncryptionBundle(
        val aliceIdentityKey: ByteArray,
        val aliceEphemeralPublicKey: ByteArray,
        val bobOpkId: Int?,
        val bobSignedPreKeyId: Int,
        val symmetricKey: ByteArray,
        val ad: ByteArray
    )

    private fun decode(string: String): ByteArray {
        return Base64.getDecoder().decode(string)
    }
}