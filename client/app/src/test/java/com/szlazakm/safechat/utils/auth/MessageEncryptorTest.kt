package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.entities.EncryptionSession
import com.szlazakm.safechat.client.data.entities.EphemeralRatchetEccKeyPairEntity
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.EphemeralRatchetKeyPairRepository
import com.szlazakm.safechat.client.data.repositories.IdentityKeyRepository
import com.szlazakm.safechat.client.data.repositories.MessageKeysRepository
import com.szlazakm.safechat.client.data.repositories.ReceiverChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.RootKeyRepository
import com.szlazakm.safechat.client.data.repositories.SenderChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.alice.AliceEncryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.alice.InitialMessageEncryptionBundle
import com.szlazakm.safechat.utils.auth.bob.BobDecryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.bob.InitialMessageDecryptionBundle
import com.szlazakm.safechat.utils.auth.ecc.AuthMessageHelper
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.helpers.EncryptionSessionCreator
import com.szlazakm.safechat.utils.auth.utils.Encoder
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import com.szlazakm.safechat.webclient.dtos.OutputEncryptedMessageDTO
import kotlinx.coroutines.test.runTest
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import java.security.Security
import java.time.Instant
import java.util.Date
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class MessageEncryptorTest {

    @Mock
    private lateinit var aliceEncryptionSessionInitializer: AliceEncryptionSessionInitializer
    @Mock
    private lateinit var rootKeyRepository: RootKeyRepository
    @Mock
    private lateinit var senderChainKeyRepository: SenderChainKeyRepository
    @Mock
    private lateinit var ephemeralRatchetKeyPairRepository: EphemeralRatchetKeyPairRepository
    @Mock
    private lateinit var identityKeyRepository: IdentityKeyRepository

    private lateinit var messageEncryptor: MessageEncryptor

    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var bobDecryptionSessionInitializer: BobDecryptionSessionInitializer
    @Mock
    private lateinit var receiverChainKeyRepository: ReceiverChainKeyRepository
    @Mock
    private lateinit var messageKeysRepository: MessageKeysRepository
    @Mock
    private lateinit var contactRepository: ContactRepository

    private lateinit var messageDecryptor: MessageDecryptor

    @Before
    fun setUp() {

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)

        MockitoAnnotations.openMocks(this) // Initialize mocks
        messageEncryptor = MessageEncryptor(
            userRepository,
            aliceEncryptionSessionInitializer,
            rootKeyRepository,
            senderChainKeyRepository,
            ephemeralRatchetKeyPairRepository,
            identityKeyRepository,
            contactRepository
        )

        messageDecryptor = MessageDecryptor(
            userRepository,
            bobDecryptionSessionInitializer,
            rootKeyRepository,
            receiverChainKeyRepository,
            senderChainKeyRepository,
            messageKeysRepository,
            identityKeyRepository,
            ephemeralRatchetKeyPairRepository
        )
    }

    fun simpleCurve25519Test() {

    }

    @Test
    fun shouldEncryptAndDecryptInitialMessage() = runTest {

        //given
        val alicePhoneNumber = "100"
        val bobPhoneNumber = "101"
        val message = "Initial message!"

        val messageDTO = MessageDTO(
            from = alicePhoneNumber,
            to = bobPhoneNumber,
            text = message,
            nonceTimestamp = Instant.now().epochSecond,
            nonce = AuthMessageHelper.generateNonce(),
            authMessageSignature = ByteArray(32),
            phoneNumber = alicePhoneNumber
        )

        val keyBundles = EncryptionSessionCreator.createKeyBundles()
        val aliceKeyBundle = keyBundles.first
        val bobKeyBundle = keyBundles.second

        val aliceLocalUserEntity = UserEntity(
            phoneNumber = alicePhoneNumber,
            firstName = "Alice",
            lastName = "Doe",
            createdAt = Date.from(Instant.now()),
            privateIdentityKey = Encoder.encode(aliceKeyBundle.identityKeyPair.privateKey),
            publicIdentityKey = Encoder.encode(aliceKeyBundle.identityKeyPair.publicKey)
        )
        val bobLocalUserEntity = UserEntity(
            phoneNumber = bobPhoneNumber,
            firstName = "Bob",
            lastName = "Doe",
            createdAt = Date.from(Instant.now()),
            privateIdentityKey = Encoder.encode(bobKeyBundle.identityKeyPair.privateKey),
            publicIdentityKey = Encoder.encode(bobKeyBundle.identityKeyPair.publicKey)
        )

        val initialMessageEncryptionBundle = InitialMessageEncryptionBundle(
            alicePublicIdentityKey = aliceKeyBundle.identityKeyPair.privateKey,
            aliceEphemeralPublicKey = aliceKeyBundle.ephemeralKeyPair.publicKey,
            bobPublicIdentityKey = bobKeyBundle.identityKeyPair.publicKey,
            bobOpkId = 1,
            bobSignedPreKeyId = 1,
            aliceEphemeralRatchetEccKeyPair = aliceKeyBundle.ratchetEccKeyPair,
            ratchetSendingChain = aliceKeyBundle.senderRatchetChain!!
        )

        val initialMessageDecryptionBundle = InitialMessageDecryptionBundle(
            aliceEphemeralRatchetEccPublicKey = aliceKeyBundle.ratchetEccKeyPair.publicKey,
            ratchetKeyPair = bobKeyBundle.senderRatchetChain!!,
            receiverChainKey = bobKeyBundle.receiverRatchetChain!!.second,
            ourIdentityPublicKey = bobKeyBundle.identityKeyPair.publicKey,
            theirIdentityPublicKey = aliceKeyBundle.identityKeyPair.publicKey,
            bobEphemeralRatchetEccKeyPair = bobKeyBundle.ratchetEccKeyPair
        )

        val bobEncryptionSession = EncryptionSessionCreator.encryptionSessionFromAliceInitialMessageEncryptionBundle(
            bobPhoneNumber, initialMessageEncryptionBundle
        )

        Log.d("bobEncryptionSession", bobEncryptionSession.receiverChainKeyEntities.toString())
        println(bobEncryptionSession.receiverChainKeyEntities.toString())

        Mockito.`when`(rootKeyRepository.getEncryptionSession(bobPhoneNumber)).thenReturn(
            null,                                                                             //return null at first because the session doesn't exist
            bobEncryptionSession                                                                                       //return session as it was initialized
        )
        Mockito.`when`(userRepository.getLocalUser()).thenReturn(aliceLocalUserEntity, bobLocalUserEntity)
        Mockito.`when`(aliceEncryptionSessionInitializer.getInitialMessageEncryptionBundle(bobPhoneNumber))
            .thenReturn(initialMessageEncryptionBundle)

        val aliceEncryptionSession = EncryptionSessionCreator.encryptionSessionFromBobInitialMessageDecryptionBundle(
            alicePhoneNumber, initialMessageDecryptionBundle
        )

        Mockito.`when`(rootKeyRepository.getEncryptionSession(alicePhoneNumber)).thenReturn(
            null,
            aliceEncryptionSession
        )

        //when
        val encryptedMessageDTO = messageEncryptor.encryptMessage(messageDTO)!!

        val outputEncryptedMessageDTO = OutputEncryptedMessageDTO(
            id = UUID.randomUUID(),
            initial = true,
            from = alicePhoneNumber,
            to = bobPhoneNumber,
            cipher = encryptedMessageDTO.cipher,
            aliceIdentityPublicKey = encryptedMessageDTO.aliceIdentityPublicKey,
            aliceEphemeralPublicKey = encryptedMessageDTO.aliceEphemeralPublicKey,
            bobOpkId = encryptedMessageDTO.bobOpkId,
            bobSpkId = encryptedMessageDTO.bobSpkId,
            date = "2024-10-13 10:00:00",
            ephemeralRatchetKey = encryptedMessageDTO.ephemeralRatchetKey,
            messageIndex = encryptedMessageDTO.messageIndex,
            lastMessageBatchSize = encryptedMessageDTO.lastMessageBatchSize
        )

        Mockito.`when`(bobDecryptionSessionInitializer.getInitialMessageEncryptionBundle(outputEncryptedMessageDTO))
            .thenReturn(initialMessageDecryptionBundle)

        val decryptedMessage = messageDecryptor.decryptMessage(outputEncryptedMessageDTO)

        //then
        println("Encrypted message DTO: $encryptedMessageDTO")
        println("Decrypted message: $decryptedMessage")

        Assert.assertEquals(message, decryptedMessage)
    }

    @Test
    fun shouldEncryptMessage() = runTest {

        //given
        val alicePhoneNumber = "100"
        val bobPhoneNumber = "101"
        val message = "Hello message!"

        val messageDTO = MessageDTO(
            from = alicePhoneNumber,
            to = bobPhoneNumber,
            text = message,
            nonceTimestamp = Instant.now().epochSecond,
            nonce = AuthMessageHelper.generateNonce(),
            authMessageSignature = ByteArray(32),
            phoneNumber = alicePhoneNumber
        )

        val keyBundles = EncryptionSessionCreator.createKeyBundles()
        val aliceKeyBundle = keyBundles.first
        val bobKeyBundle = keyBundles.second

        val encryptionSessions = EncryptionSessionCreator.createEncryptionSessions(
            alicePhoneNumber, bobPhoneNumber, aliceKeyBundle, bobKeyBundle
        )

        val aliceLocalUserEntity = UserEntity(
            phoneNumber = alicePhoneNumber,
            firstName = "Alice",
            lastName = "Doe",
            createdAt = Date.from(Instant.now()),
            privateIdentityKey = Encoder.encode(aliceKeyBundle.identityKeyPair.privateKey),
            publicIdentityKey = Encoder.encode(aliceKeyBundle.identityKeyPair.publicKey)
        )
        val bobLocalUserEntity = UserEntity(
            phoneNumber = bobPhoneNumber,
            firstName = "Bob",
            lastName = "Doe",
            createdAt = Date.from(Instant.now()),
            privateIdentityKey = Encoder.encode(bobKeyBundle.identityKeyPair.privateKey),
            publicIdentityKey = Encoder.encode(bobKeyBundle.identityKeyPair.publicKey)
        )

        val ephemeralRatchetEccKeyPairEntity = EphemeralRatchetEccKeyPairEntity(
            phoneNumber = alicePhoneNumber,
            publicKey = bobKeyBundle.ratchetEccKeyPair.publicKey,
            privateKey = bobKeyBundle.ratchetEccKeyPair.privateKey
        )

        Mockito.`when`(rootKeyRepository.getEncryptionSession(bobPhoneNumber)).thenReturn(encryptionSessions.first)
        Mockito.`when`(rootKeyRepository.getEncryptionSession(alicePhoneNumber)).thenReturn(encryptionSessions.second)
        Mockito.`when`(userRepository.getLocalUser()).thenReturn(aliceLocalUserEntity, bobLocalUserEntity)
        Mockito.`when`(ephemeralRatchetKeyPairRepository.getEphemeralRatchetKeyPair(alicePhoneNumber)).thenReturn(ephemeralRatchetEccKeyPairEntity)

        //when
        val encryptedMessageDTO = messageEncryptor.encryptMessage(messageDTO)!!

        val outputEncryptedMessageDTO = OutputEncryptedMessageDTO(
            id = UUID.randomUUID(),
            initial = false,
            from = alicePhoneNumber,
            to = bobPhoneNumber,
            cipher = encryptedMessageDTO.cipher,
            aliceIdentityPublicKey = null,
            aliceEphemeralPublicKey = null,
            bobOpkId = null,
            bobSpkId = null,
            date = "2024-10-13 10:00:00",
            ephemeralRatchetKey = encryptedMessageDTO.ephemeralRatchetKey,
            messageIndex = encryptedMessageDTO.messageIndex,
            lastMessageBatchSize = encryptedMessageDTO.lastMessageBatchSize
        )

        val decryptedMessage = messageDecryptor.decryptMessage(outputEncryptedMessageDTO)!!

        //then
        println("Cipher: ${encryptedMessageDTO.cipher}")
        println("Decrypted message: $decryptedMessage")

        Assert.assertEquals(message, decryptedMessage)
    }
}