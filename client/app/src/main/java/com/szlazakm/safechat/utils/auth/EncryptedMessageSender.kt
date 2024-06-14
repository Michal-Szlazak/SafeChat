package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.EncryptionSessionEntity
import com.szlazakm.safechat.client.data.repositories.EncryptionSessionRepository
import com.szlazakm.safechat.webclient.dtos.EncryptedMessageDTO
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedMessageSender @Inject constructor(
    private val aliceEncryptionSessionInitializer: AliceEncryptionSessionInitializer,
    private val encryptionSessionRepository: EncryptionSessionRepository
) {

    suspend fun encryptMessage(messageDTO: MessageDTO) : EncryptedMessageDTO? {

        val receiverPhoneNumber = messageDTO.to
        val encryptionSession = encryptionSessionRepository.getEncryptionSessionByPhoneNumber(receiverPhoneNumber)

        val symmetricKey: ByteArray
        val ad: ByteArray

        if(encryptionSession == null) {
            Log.d("EncryptedMessageSender", "Encryption session is null")
            val initialMessageEncryptionBundle = aliceEncryptionSessionInitializer
                .getInitialMessageEncryptionBundle(receiverPhoneNumber)

            if(initialMessageEncryptionBundle == null) {
                Log.e("EncryptedMessageSender", "Failed to get initial message encryption bundle.")
                return null
            }

            symmetricKey = initialMessageEncryptionBundle.symmetricKey
            ad = initialMessageEncryptionBundle.ad

            createNewSession(receiverPhoneNumber, symmetricKey, ad)

            val cipherText = encryptMessage(symmetricKey, ad, messageDTO.text)

            return EncryptedMessageDTO(
                initial = true,
                messageDTO.from,
                messageDTO.to,
                encode(cipherText),
                encode(initialMessageEncryptionBundle.aliceIdentityKey),
                encode(initialMessageEncryptionBundle.aliceEphemeralPublicKey),
                initialMessageEncryptionBundle.bobOpkId,
                initialMessageEncryptionBundle.bobSignedPreKeyId
            )

        } else {

            Log.d("EncryptedMessageSender", "Encryption session is not null $encryptionSession")

            symmetricKey = decode(encryptionSession.symmetricKey)
            ad = decode(encryptionSession.ad)

            val cipherText = encryptMessage(symmetricKey, ad, messageDTO.text)

            return EncryptedMessageDTO(
                initial = false,
                messageDTO.from,
                messageDTO.to,
                encode(cipherText),
                null,
                null,
                null,
                null
            )
        }
    }

    private suspend fun createNewSession(
        phoneNumber: String,
        symmetricKey: ByteArray,
        ad: ByteArray
    ) {
        val encryptionSessionEntity = EncryptionSessionEntity(
            phoneNumber,
            encode(symmetricKey),
            encode(ad)
        )
        (Dispatchers.IO) {
            encryptionSessionRepository.createNewEncryptionSession(encryptionSessionEntity)
        }
    }

    private fun encryptMessage(
        symmetricKey: ByteArray,
        ad: ByteArray,
        message: String
    ) : ByteArray {
        val iv = ByteArray(12) // 12 bytes IV for AES-GCM
        SecureRandom().nextBytes(iv)

        // Create AES-GCM cipher instance
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(symmetricKey, "AES")
        val gcmParams = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParams)

        // Encrypt the plaintext with AES-GCM
        val ciphertext = cipher.doFinal(message.encodeToByteArray())

        // Concatenate header and ciphertext
        val encryptedMessage = ByteArray(ad.size + iv.size + ciphertext.size)
        System.arraycopy(ad, 0, encryptedMessage, 0, ad.size)
        System.arraycopy(iv, 0, encryptedMessage, ad.size, iv.size)
        System.arraycopy(ciphertext, 0, encryptedMessage, ad.size + iv.size, ciphertext.size)

        return encryptedMessage
    }

    private fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun decode(encoded: String): ByteArray {
        return Base64.getDecoder().decode(encoded)
    }
}