package com.szlazakm.safechat.utils.auth

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.szlazakm.safechat.client.data.entities.EncryptionSession
import com.szlazakm.safechat.client.data.entities.EphemeralRatchetEccKeyPairEntity
import com.szlazakm.safechat.client.data.entities.IdentityKeyEntity
import com.szlazakm.safechat.client.data.entities.MessageKeysEntity
import com.szlazakm.safechat.client.data.entities.ReceiverChainKeyEntity
import com.szlazakm.safechat.client.data.entities.RootKeyEntity
import com.szlazakm.safechat.client.data.entities.SenderChainKeyEntity
import com.szlazakm.safechat.client.data.repositories.EphemeralRatchetKeyPairRepository
import com.szlazakm.safechat.client.data.repositories.IdentityKeyRepository
import com.szlazakm.safechat.client.data.repositories.MessageKeysRepository
import com.szlazakm.safechat.client.data.repositories.ReceiverChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.RootKeyRepository
import com.szlazakm.safechat.client.data.repositories.SenderChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.bob.BobDecryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.bob.InitialMessageDecryptionBundle
import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.ecc.MessageKeys
import com.szlazakm.safechat.utils.auth.ecc.ReceiverChainKey
import com.szlazakm.safechat.utils.auth.ecc.RootKey
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.DiffieHellman
import com.szlazakm.safechat.utils.auth.utils.MAC
import com.szlazakm.safechat.utils.auth.utils.MAC_SIZE
import com.szlazakm.safechat.utils.auth.utils.PaddingHandler
import com.szlazakm.safechat.webclient.dtos.OutputEncryptedMessageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MessageDecryptor @Inject constructor(
    private val userRepository: UserRepository,
    private val bobDecryptionSessionInitializer: BobDecryptionSessionInitializer,
    private val rootKeyRepository: RootKeyRepository,
    private val receiverChainKeyRepository: ReceiverChainKeyRepository,
    private val senderChainKeyRepository: SenderChainKeyRepository,
    private val messageKeysRepository: MessageKeysRepository,
    private val identityKeyRepository: IdentityKeyRepository,
    private val ephemeralRatchetKeyPairRepository: EphemeralRatchetKeyPairRepository
) {

    private val mutex = Mutex()

    suspend fun decryptMessage(encryptedMessage: OutputEncryptedMessageDTO): String? {

        mutex.withLock {

            val senderPhoneNumber = encryptedMessage.from
            var encryptionSession =
                rootKeyRepository.getEncryptionSession(senderPhoneNumber)

            if (encryptedMessage.initial) {

                val initialMessageEncryptionBundle =
                    bobDecryptionSessionInitializer.getInitialMessageEncryptionBundle(
                        encryptedMessage
                    )

                if (initialMessageEncryptionBundle == null) {
                    Log.e("SafeChat:MessageDecryptor", "Failed to create shared secret.")
                    return null
                }

                Log.d(
                    "SafeChat:MessageDecryptor",
                    "created receiver chain key: ${initialMessageEncryptionBundle.receiverChainKey.key.toHex()}"
                )

                encryptionSession = createNewSession(
                    phoneNumber = senderPhoneNumber,
                    initialMessageDecryptionBundle = initialMessageEncryptionBundle
                )
            }

            val ourIdentityPublicKey = userRepository.getLocalUser().publicIdentityKey

            if (encryptionSession == null) {
                Log.e("SafeChat:MessageDecryptor", "Encryption session not found.")
                return null
            }

            Log.d("SafeChat:MessageDecryptor", "Encryption session is not null $encryptionSession")

            val chainKey = getOrCreateChainKey(
                encryptionSession,
                Decoder.decode(encryptedMessage.ephemeralRatchetKey)
            )

            Log.d("SafeChat:MessageDecryptor", "Receiver chain key: ${chainKey.key.toHex()}")

            val decryptedBytes = decryptMessage(
                session = encryptionSession,
                chainKey = chainKey,
                theirIdentityKey = encryptionSession.identityKeyEntity.publicKey,
                ourIdentityKey = Decoder.decode(ourIdentityPublicKey),
                encryptedMessageDto = encryptedMessage
            )

            return String(decryptedBytes, StandardCharsets.UTF_8)
        }
    }

    private suspend fun getOrCreateChainKey(session: EncryptionSession, ephemeralRatchetKey: ByteArray): ReceiverChainKey {

        for(chainKey in session.receiverChainKeyEntities) {
            if(chainKey.publicEphemeralKey.contentEquals(ephemeralRatchetKey)) {

                Log.d("SafeChat:MessageDecryptor", "found a receiving chainKey: ${chainKey.chainKey.toHex()}")
                return ReceiverChainKey(
                    key = chainKey.chainKey,
                    index = chainKey.chainKeyIndex,
                    publicEphemeralKey = chainKey.publicEphemeralKey
                )
            }
        }

        Log.d("SafeChat:MessageDecryptor", "did not find a receiving chainKey")

        var rootKey = RootKey(
            session.rootKeyEntity.rootKey
        )

        val newRatchetKeyPair = EccKeyHelper.generateKeyPair()
        var oldEphemeralRatchetEccKeyPairEntity: EphemeralRatchetEccKeyPairEntity? = null

        (Dispatchers.IO) {
            oldEphemeralRatchetEccKeyPairEntity = ephemeralRatchetKeyPairRepository.getEphemeralRatchetKeyPair(
                session.rootKeyEntity.phoneNumber
            )
        }

        val receiverKeyPair = rootKey.createChain(
            theirRatchetPublicKey = ephemeralRatchetKey,
            ourRatchetPrivateKey = oldEphemeralRatchetEccKeyPairEntity!!.privateKey
        )

        Log.d("SafeChat:MessageDecryptor", "created receiving chainKey: ${receiverKeyPair.second.key.toHex()}")

        rootKey = receiverKeyPair.first

        val senderKeyPair = rootKey.createChain(
            theirRatchetPublicKey = ephemeralRatchetKey,
            ourRatchetPrivateKey = newRatchetKeyPair.privateKey
        )

        (Dispatchers.IO) {

            val ephemeralRatchetEccKeyPairEntity = EphemeralRatchetEccKeyPairEntity(
                phoneNumber = session.rootKeyEntity.phoneNumber,
                publicKey = newRatchetKeyPair.publicKey,
                privateKey = newRatchetKeyPair.privateKey
            )

            val newRootKeyEntity = RootKeyEntity(
                phoneNumber = session.rootKeyEntity.phoneNumber,
                rootKey = senderKeyPair.first.key
            )

            val newReceiverChainKeyEntity = ReceiverChainKeyEntity(
                chainKey = receiverKeyPair.second.key,
                publicEphemeralKey = ephemeralRatchetKey,
                chainKeyIndex = 0,
                phoneNumber = session.rootKeyEntity.phoneNumber
            )

            val newSenderChainKeyEntity = SenderChainKeyEntity(
                phoneNumber = session.rootKeyEntity.phoneNumber,
                chainKey = senderKeyPair.second.key,
                chainKeyIndex = 0,
                lastMessageBatchSize = session.senderChainKeyEntity.chainKeyIndex,
                id = session.senderChainKeyEntity.id
            )

            ephemeralRatchetKeyPairRepository.updateEphemeralRatchetKeyPair(ephemeralRatchetEccKeyPairEntity)
            rootKeyRepository.updateRootKey(newRootKeyEntity)
            senderChainKeyRepository.updateChainKey(newSenderChainKeyEntity)
            receiverChainKeyRepository.createChainKey(newReceiverChainKeyEntity)
        }

        return ReceiverChainKey(
            key = receiverKeyPair.second.key,
            index = 0,
            publicEphemeralKey =  ephemeralRatchetKey
        )
    }

    private suspend fun decryptMessage(
        session: EncryptionSession,
        chainKey: ChainKey,
        theirIdentityKey: ByteArray,
        ourIdentityKey: ByteArray,
        encryptedMessageDto: OutputEncryptedMessageDTO
    ): ByteArray {

        val messageKeys = getOrCreateMessageKeys(
            session = session,
            chainKey = chainKey,
            ephemeralRatchetKey = Decoder.decode(encryptedMessageDto.ephemeralRatchetKey),
            messageIndex = encryptedMessageDto.messageIndex
        )

        val encryptedMessageCipher = Decoder.decode(encryptedMessageDto.cipher)
        val encryptedMessage = encryptedMessageCipher.copyOfRange(0, encryptedMessageCipher.size - MAC_SIZE)
        val theirMac = encryptedMessageCipher.copyOfRange(encryptedMessageCipher.size - MAC_SIZE, encryptedMessageCipher.size)

        val isMacCorrect = MAC.verifyMac(
            messageKeys = messageKeys,
            senderPublicKey = theirIdentityKey,
            receiverPublicKey = ourIdentityKey,
            encryptedMessage = encryptedMessage,
            theirMac = theirMac
        )

        if(!isMacCorrect) {
            Log.e("SafeChat:MessageDecryptor", "The mac attached to the cipher was incorrect.")
            throw IllegalArgumentException("The mac attached to the cipher was incorrect.")
        }

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, messageKeys.getCipherKey(), messageKeys.getIv())

        val decryptedPaddedMessage = cipher.doFinal(encryptedMessage)
        return PaddingHandler.removePadding(decryptedPaddedMessage)
    }

    private suspend fun getOrCreateMessageKeys(
        session: EncryptionSession, chainKey: ChainKey, ephemeralRatchetKey: ByteArray, messageIndex: Int
    ): MessageKeys {

        if(chainKey.index > messageIndex) {
            val messageKeys = getAndRemoveMessageKeys(session, ephemeralRatchetKey, messageIndex)

            if(messageKeys == null) {
                Log.e("SafeChat:MessageDecryptor", "MessageKeys not found in the saved MessageKeys." +
                        " The MessageKeys might not been saved, or the message was already received.")
                throw NotFoundException("MessageKeys not found for ${session.rootKeyEntity.phoneNumber}" +
                        " with message index $messageIndex")
            }

            return messageKeys
        }

        var rotatingChainKey = chainKey
        var messageKeys: MessageKeys? = null

        for(index in chainKey.index..messageIndex) {

            messageKeys = rotatingChainKey.getMessageKeys()
            val messageKeysEntity = MessageKeysEntity(
                id = 0, // its autogenerated
                ephemeralRatchetKey = ephemeralRatchetKey,
                phoneNumber = session.rootKeyEntity.phoneNumber,
                cipherKey = messageKeys.getCipherKey().encoded,
                macKey = messageKeys.getMacKey().encoded,
                iv = messageKeys.getIv().iv,
                index = index
            )

            messageKeysRepository.createMessageKeys(messageKeysEntity)
            rotatingChainKey = rotatingChainKey.getNextChainKey()
        }

        val lastReceiverChainKeyEntity = ReceiverChainKeyEntity(
            chainKey = rotatingChainKey.key,
            publicEphemeralKey = ephemeralRatchetKey,
            chainKeyIndex = rotatingChainKey.index,
            phoneNumber = session.rootKeyEntity.phoneNumber
        )
        receiverChainKeyRepository.updateChainKey(lastReceiverChainKeyEntity)

        if(messageKeys == null) {
            Log.e("SafeChat:MessageDecryptor", "Failed to create MessageKeys.")
            throw NotFoundException("Failed to create MessageKeys for ${session.rootKeyEntity.phoneNumber}" +
                    " with message index $messageIndex")
        }

        return messageKeys
    }

    private suspend fun getAndRemoveMessageKeys(session: EncryptionSession, ephemeralRatchetKey: ByteArray, messageIndex: Int): MessageKeys? {

        val phoneNumber = session.rootKeyEntity.phoneNumber
        var toReturn: MessageKeys? = null

        (Dispatchers.IO) {

            val messageKeysList = messageKeysRepository.getMessageKeys(phoneNumber, ephemeralRatchetKey)

            for(messageKeys in messageKeysList) {

                if(messageKeys.index == messageIndex) {
                    toReturn = MessageKeys(
                        cipherKey = SecretKeySpec(messageKeys.cipherKey, "AES"),
                        macKey = SecretKeySpec(messageKeys.macKey, "HmacSHA256"),
                        iv = IvParameterSpec(messageKeys.iv),
                        index = messageKeys.index
                    )

                    messageKeysRepository.deleteMessageKeysDao(messageKeys)
                }
            }
        }

        return toReturn
    }

    private suspend fun createNewSession(
        phoneNumber: String,
        initialMessageDecryptionBundle: InitialMessageDecryptionBundle
    ): EncryptionSession {

        Log.d("SafeChat:MessageDecryptor", "create new session called")

        val rootKeyEntity = RootKeyEntity(
            phoneNumber,
            initialMessageDecryptionBundle.ratchetKeyPair.first.key
        )

        val senderChainKeyEntity = SenderChainKeyEntity(
            phoneNumber = phoneNumber,
            chainKey = initialMessageDecryptionBundle.ratchetKeyPair.second.key,
            chainKeyIndex = 0,
            lastMessageBatchSize = 0,
            id = 0 //overwritten by db
        )

        val receiverChainKeyEntity = ReceiverChainKeyEntity(
            phoneNumber = phoneNumber,
            chainKey = initialMessageDecryptionBundle.receiverChainKey.key,
            publicEphemeralKey = initialMessageDecryptionBundle.receiverChainKey.publicEphemeralKey,
            chainKeyIndex = initialMessageDecryptionBundle.receiverChainKey.index
        )

        val identityKeyEntity = IdentityKeyEntity(
            phoneNumber = phoneNumber,
            publicKey = initialMessageDecryptionBundle.theirIdentityPublicKey
        )

        val ephemeralRatchetEccKeyPairEntity = EphemeralRatchetEccKeyPairEntity(
            phoneNumber = phoneNumber,
            publicKey = initialMessageDecryptionBundle.bobEphemeralRatchetEccKeyPair.publicKey,
            privateKey = initialMessageDecryptionBundle.bobEphemeralRatchetEccKeyPair.privateKey
        )

        return withContext(Dispatchers.IO) {
            rootKeyRepository.createRootKey(rootKeyEntity)
            senderChainKeyRepository.createChainKey(senderChainKeyEntity)
            receiverChainKeyRepository.createChainKey(receiverChainKeyEntity)
            identityKeyRepository.createIdentityKey(identityKeyEntity)
            ephemeralRatchetKeyPairRepository.createEphemeralRatchetKeyPair(ephemeralRatchetEccKeyPairEntity)

            rootKeyRepository.getEncryptionSession(phoneNumber)!!
        }
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}