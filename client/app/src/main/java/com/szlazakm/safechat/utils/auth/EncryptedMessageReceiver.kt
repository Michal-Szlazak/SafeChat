package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.EncryptionSessionEntity
import com.szlazakm.safechat.client.data.repositories.EncryptionSessionRepository
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.Encoder
import com.szlazakm.safechat.webclient.dtos.OutputEncryptedMessageDTO
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedMessageReceiver @Inject constructor(
    private val encryptionSessionRepository: EncryptionSessionRepository,
    private val bobDecryptionSessionInitializer: BobDecryptionSessionInitializer
) {

    suspend fun decryptMessage(encryptedMessage: OutputEncryptedMessageDTO): String? {

        val senderPhoneNumber = encryptedMessage.from
        var encryptionSession =
            encryptionSessionRepository.getEncryptionSessionByPhoneNumber(senderPhoneNumber)

        var symmetricKey: ByteArray?
        var ad: ByteArray

        if (encryptedMessage.initial) {

            if (encryptionSession != null) {
                Log.e("EncryptedMessageReceiver", "Encryption session already exists. Deleting...")
                encryptionSessionRepository.deleteEncryptionSessionByPhoneNumber(senderPhoneNumber)
            }

            val bobInitializeSessionBundle =
                bobDecryptionSessionInitializer.createSymmetricKey(encryptedMessage)

            if (bobInitializeSessionBundle == null) {
                Log.e("EncryptedMessageReceiver", "Failed to create shared secret.")
                return null
            }

            symmetricKey = bobInitializeSessionBundle.symmetricKey
            ad = bobInitializeSessionBundle.ad

            val newEncryptionSession = EncryptionSessionEntity(
                phoneNumber = senderPhoneNumber,
                Encoder.encode(symmetricKey),
                Encoder.encode(ad)
            )

            encryptionSessionRepository.createNewEncryptionSession(newEncryptionSession)
            encryptionSession = newEncryptionSession

        }

        if (encryptionSession == null) {
            Log.e("EncryptedMessageReceiver", "Encryption session not found.")
            return null
        }

        symmetricKey = Decoder.decode(encryptionSession.symmetricKey)
        ad = Decoder.decode(encryptionSession.ad)

        return decryptMessage(symmetricKey, ad, Decoder.decode(encryptedMessage.cipher))
    }

    private fun decryptMessage(symmetricKey: ByteArray, ad: ByteArray, encryptedMessage: ByteArray): String {

        // Extract IV and ciphertext from the encrypted message
        val ivSize = 12 // Size of IV for AES-GCM
        val iv = encryptedMessage.copyOfRange(ad.size, ad.size + ivSize)
        val ciphertext = encryptedMessage.copyOfRange(ad.size + ivSize, encryptedMessage.size)

        // Initialize AES-GCM cipher for decryption
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(symmetricKey, "AES")
        val gcmParams = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParams)

        // Decrypt the ciphertext
        val plaintext = cipher.doFinal(ciphertext)

        // Convert decrypted bytes to String
        return plaintext.decodeToString()

    }
}