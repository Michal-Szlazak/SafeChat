package com.szlazakm.safechat.utils.auth

import android.content.res.Resources
import android.util.Log
import com.szlazakm.safechat.client.data.entities.EncryptionSession
import com.szlazakm.safechat.client.data.entities.EphemeralRatchetEccKeyPairEntity
import com.szlazakm.safechat.client.data.entities.IdentityKeyEntity
import com.szlazakm.safechat.client.data.entities.RootKeyEntity
import com.szlazakm.safechat.client.data.entities.SenderChainKeyEntity
import com.szlazakm.safechat.client.data.repositories.EphemeralRatchetKeyPairRepository
import com.szlazakm.safechat.client.data.repositories.IdentityKeyRepository
import com.szlazakm.safechat.client.data.repositories.RootKeyRepository
import com.szlazakm.safechat.client.data.repositories.SenderChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.alice.AliceEncryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.alice.InitialMessageEncryptionBundle
import com.szlazakm.safechat.utils.auth.ecc.AuthMessageHelper
import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.Encoder
import com.szlazakm.safechat.utils.auth.utils.MAC
import com.szlazakm.safechat.utils.auth.utils.PaddingHandler
import com.szlazakm.safechat.webclient.dtos.EncryptedMessageDTO
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.time.Instant
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageEncryptor @Inject constructor(
    private val userRepository: UserRepository,
    private val aliceEncryptionSessionInitializer: AliceEncryptionSessionInitializer,
    private val rootKeyRepository: RootKeyRepository,
    private val senderChainKeyRepository: SenderChainKeyRepository,
    private val ephemeralRatchetKeyPairRepository: EphemeralRatchetKeyPairRepository,
    private val identityKeyRepository: IdentityKeyRepository
) {

    suspend fun encryptMessage(messageDTO: MessageDTO) : EncryptedMessageDTO? {

        val receiverPhoneNumber = messageDTO.to
        val encryptionSession = rootKeyRepository.getEncryptionSession(receiverPhoneNumber)

        val localUser = userRepository.getLocalUser()

        if(encryptionSession == null) {
            Log.d("MessageEncryptor", "Encryption session is null")

            val initialMessageEncryptionBundle = aliceEncryptionSessionInitializer
                .getInitialMessageEncryptionBundle(receiverPhoneNumber)

            val session = createNewSession(receiverPhoneNumber, initialMessageEncryptionBundle)

            var chainKey = initialMessageEncryptionBundle.ratchetSendingChain.second

            Log.d("SafeChat:MessageEncryptor", "Sending chain key: ${chainKey.key.toHex()}")

            val cipherText = encryptMessage(
                chainKey,
                messageDTO.text
            )

            val mac = MAC.createMac(
                chainKey.getMessageKeys(),
                receiverPublicKey = session.identityKeyEntity.publicKey,
                senderPublicKey = Decoder.decode(localUser.publicIdentityKey),
                encryptedMessage = cipherText
            )

            val nonce =  AuthMessageHelper.generateNonce()
            val instant = Instant.now().epochSecond.toString()
            val privateKeyBytes = Decoder.decode(userRepository.getLocalUser().privateIdentityKey)
            val dataToSign = nonce.plus(Decoder.decode(instant))
            val signature = AuthMessageHelper.generateSignature(
                privateKeyBytes,
                dataToSign
            )

            val encryptedMessageDTO = EncryptedMessageDTO(
                initial = true,
                messageDTO.from,
                messageDTO.to,
                Encoder.encode(cipherText + mac ),
                Encoder.encode(initialMessageEncryptionBundle.alicePublicIdentityKey),
                Encoder.encode(initialMessageEncryptionBundle.aliceEphemeralPublicKey),
                initialMessageEncryptionBundle.bobOpkId,
                initialMessageEncryptionBundle.bobSignedPreKeyId,
                Encoder.encode(initialMessageEncryptionBundle.aliceEphemeralRatchetEccKeyPair.publicKey),
                chainKey.index,
                session.senderChainKeyEntity.lastMessageBatchSize,
                localUser.phoneNumber,
                instant.toLong(),
                nonce,
                signature
            )

            chainKey = chainKey.getNextChainKey()

            val updatedChainKeyEntity = SenderChainKeyEntity(
                phoneNumber = receiverPhoneNumber,
                chainKey = chainKey.key,
                chainKeyIndex = 1,
                lastMessageBatchSize = 0,
                id = 0 //overwritten by db
            )
            senderChainKeyRepository.updateChainKey(updatedChainKeyEntity)

            return encryptedMessageDTO

        } else {

            Log.d("SafeChat:MessageEncryptor", "Encryption session is not null $encryptionSession")

            val chainKeyEntity = encryptionSession.senderChainKeyEntity
            var chainKey = ChainKey(
                chainKeyEntity.chainKey,
                chainKeyEntity.chainKeyIndex
            )

            Log.d("SafeChat:MessageEncryptor", "sender chain key: ${chainKey.key.toHex()}")

            val cipherText = encryptMessage(
                chainKey,
                messageDTO.text
            )

            val mac = MAC.createMac(
                messageKeys = chainKey.getMessageKeys(),
                receiverPublicKey = encryptionSession.identityKeyEntity.publicKey,
                senderPublicKey = Decoder.decode(localUser.publicIdentityKey),
                encryptedMessage = cipherText
            )
            val nonce =  AuthMessageHelper.generateNonce()
            val instant = Instant.now().epochSecond.toString()
            val privateKeyBytes = Decoder.decode(userRepository.getLocalUser().privateIdentityKey)
            val dataToSign = nonce.plus(Decoder.decode(instant))
            val signature = AuthMessageHelper.generateSignature(
                privateKeyBytes,
                dataToSign
            )

            val encryptedMessageDTO = EncryptedMessageDTO(
                initial = false,
                from = messageDTO.from,
                to = messageDTO.to,
                cipher = Encoder.encode(cipherText + mac),
                aliceIdentityPublicKey = null,
                aliceEphemeralPublicKey = null,
                bobOpkId = null,
                bobSpkId = null,
                ephemeralRatchetKey = Encoder.encode(encryptionSession.ephemeralRatchetEccKeyPairEntity.publicKey),
                messageIndex = chainKey.index,
                lastMessageBatchSize = chainKeyEntity.lastMessageBatchSize,
                phoneNumber = localUser.phoneNumber,
                nonceTimestamp = instant.toLong(),
                nonce = nonce,
                authMessageSignature = signature
            )

            chainKey = chainKey.getNextChainKey()

            val updatedChainKeyEntity = SenderChainKeyEntity(
                phoneNumber = chainKeyEntity.phoneNumber,
                chainKey = chainKey.key,
                chainKeyIndex = chainKeyEntity.chainKeyIndex + 1,
                lastMessageBatchSize = chainKeyEntity.lastMessageBatchSize,
                id = chainKeyEntity.id
            )
            senderChainKeyRepository.updateChainKey(updatedChainKeyEntity)

            return encryptedMessageDTO
        }
    }

    private suspend fun createNewSession(
        phoneNumber: String,
        initialMessageEncryptionBundle: InitialMessageEncryptionBundle
    ): EncryptionSession {

        val rootKeyEntity = RootKeyEntity(
            phoneNumber,
            initialMessageEncryptionBundle.ratchetSendingChain.first.key
        )

        val senderChainKeyEntity = SenderChainKeyEntity(
            phoneNumber = phoneNumber,
            chainKey = initialMessageEncryptionBundle.ratchetSendingChain.second.key,
            chainKeyIndex = 0,
            lastMessageBatchSize = 0,
            id = 0 //overwritten by db
        )

        val ephemeralRatchetEccKeyPairEntity = EphemeralRatchetEccKeyPairEntity(
            phoneNumber = phoneNumber,
            publicKey = initialMessageEncryptionBundle.aliceEphemeralRatchetEccKeyPair.publicKey,
            privateKey = initialMessageEncryptionBundle.aliceEphemeralRatchetEccKeyPair.privateKey
        )

        val identityKeyEntity = IdentityKeyEntity(
            phoneNumber = phoneNumber,
            publicKey = initialMessageEncryptionBundle.bobPublicIdentityKey
        )

        return withContext(Dispatchers.IO) {
            rootKeyRepository.createRootKey(rootKeyEntity)
            senderChainKeyRepository.createChainKey(senderChainKeyEntity)
            ephemeralRatchetKeyPairRepository.createEphemeralRatchetKeyPair(ephemeralRatchetEccKeyPairEntity)
            identityKeyRepository.createIdentityKey(identityKeyEntity)

            rootKeyRepository.getEncryptionSession(phoneNumber)
                ?: throw Resources.NotFoundException("Failed to retrieve new session after creation.")
        }
    }

    private fun encryptMessage(
        chainKey: ChainKey,
        message: String
    ) : ByteArray {

        val messageKeys = chainKey.getMessageKeys()

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, messageKeys.getCipherKey(), messageKeys.getIv())

        val messageBytes = message.toByteArray(StandardCharsets.UTF_8)

        val paddedMessageBytes = PaddingHandler.addPadding(messageBytes)

        return cipher.doFinal(paddedMessageBytes)
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}