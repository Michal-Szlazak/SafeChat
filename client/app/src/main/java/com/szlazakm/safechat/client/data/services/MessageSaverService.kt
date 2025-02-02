package com.szlazakm.safechat.client.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.szlazakm.safechat.R
import com.szlazakm.safechat.client.data.entities.MessageEntity
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.IdentityKeyRepository
import com.szlazakm.safechat.client.data.repositories.MessageRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.utils.auth.MessageDecryptor
import com.szlazakm.safechat.utils.auth.ecc.AuthMessageHelper
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.Encoder
import com.szlazakm.safechat.webclient.dtos.GetMessagesDTO
import com.szlazakm.safechat.webclient.dtos.MessageAcknowledgementDTO
import com.szlazakm.safechat.webclient.dtos.OutputEncryptedMessageDTO
import com.szlazakm.safechat.webclient.dtos.UserDTO
import com.szlazakm.safechat.webclient.webservices.ChatWebService
import com.szlazakm.safechat.webclient.webservices.UserWebService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class MessageSaverService : Service(){

    @Inject
    lateinit var contactRepository: ContactRepository
    @Inject
    lateinit var messageRepository: MessageRepository
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var retrofit: Retrofit
    @Inject
    lateinit var messageDecryptor: MessageDecryptor

    @Inject
    lateinit var identityKeyRepository: IdentityKeyRepository

    private val stompService: StompService = StompService()
    private val gson: Gson = Gson()
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    private var messageListener: MessageListener? = null
    private var contactListener: ContactListener? = null

    private lateinit var networkMonitor: NetworkMonitor

    fun setMessageListener(listener: MessageListener) {
        messageListener = listener
    }

    fun setContactListener(listener: ContactListener) {
        contactListener = listener
    }

    companion object {
        private var instance: MessageSaverService? = null

        fun getInstance(): MessageSaverService? {
            return instance
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SafeChat:MessageSaverService", "onStart called")
        serviceScope.launch{
            connectToUserQueue()
        }

        return START_STICKY
    }

    override fun onCreate() {
        instance = this
        super.onCreate()

        networkMonitor = NetworkMonitor(this)

        networkMonitor.registerNetworkCallback(
            onNetworkAvailable = {
                reconnectStompService()
                Log.d("MessageSaverService", "Network available.")
                messageListener?.afterRecovery()
            },
            onNetworkLost = {
                stompService.disconnect()
                Log.d("MessageSaverService", "Network lost. Stomp service will be disconnected.")
            }
        )

        Log.d("SafeChat:MessageSaverService", "onCreate called")
    }

    private fun reconnectStompService() {
        Log.d("MessageSaverService", "Network available. Reconnecting STOMP service...")
        serviceScope.launch {
            connectToUserQueue()
        }
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
        stompService.disconnect()
        Log.d("SafeChat:MessageSaverService", "onDestroy called")
    }

    private suspend fun connectToUserQueue() {

        val localUser: UserEntity

        try {
            localUser = userRepository.getLocalUser()
        } catch (e: Exception) {
            Log.e(
                "SafeChat:MessageSaverService",
                "Exception while trying to get local user. Aborting MessageServiceCreation."
            )
            return
        }


        val newMessages = getNewMessages(localUser.phoneNumber)
        loadNewMessagesFromDB(newMessages ?: emptyList())

        Log.d("SafeChat:MessageSaverService", "Subscribing to user queue: ${localUser.phoneNumber}")

        stompService.subscribeToTopic("/user/queue/${localUser.phoneNumber}") { message ->

            Log.d("SafeChat:MessageSaverService","received a message $message")

            serviceScope.launch {
                val outputEncryptedMessageDTO = gson.fromJson(message, OutputEncryptedMessageDTO::class.java)
                handleNewMessage(outputEncryptedMessageDTO)
            }

        }
    }

    private suspend fun loadNewMessagesFromDB(messages: List<OutputEncryptedMessageDTO>) {

        messages.forEach{
            withContext(Dispatchers.IO) {
                val decryptedMessage = decryptMessage(it)

                if(decryptedMessage == null) {
                    Log.e(
                        "SafeChat:MessageSaverService",
                        "Failed to decrypt message from: ${it.from}"
                    )
                    return@withContext
                } else{
                    Log.d(
                        "SafeChat:MessageSaverService",
                        "Successfully decrypted message from: ${it.from}" +
                                " message: $decryptedMessage")
                }

                saveNewMessage(decryptedMessage)
            }
        }
    }

    private suspend fun handleNewMessage(outputEncryptedMessageDTO: OutputEncryptedMessageDTO) {
        try {
            val decryptedMessage = decryptMessage(outputEncryptedMessageDTO)

            if(decryptedMessage == null) {
                Log.e(
                    "SafeChat:MessageSaverService",
                    "Failed to decrypt message from: ${outputEncryptedMessageDTO.from}"
                )

                return
            } else{
                Log.d(
                    "SafeChat:MessageSaverService",
                    "Successfully decrypted message from: ${outputEncryptedMessageDTO.from}" +
                            " message: $decryptedMessage")
            }

            messageListener?.onNewMessage(decryptedMessage)

            saveNewMessage(decryptedMessage)
        } catch (e: Exception) {
            Log.e("SafeChat:MessageSaverService", "Exception thrown while handling new message: ${e.message}")
        } finally {

            val nonce =  AuthMessageHelper.generateNonce()
            val instant = Instant.now().epochSecond.toString()
            val privateKeyBytes = Decoder.decode(userRepository.getLocalUser().privateIdentityKey)
            val dataToSign = nonce.plus(Decoder.decode(instant))
            val signature = AuthMessageHelper.generateSignature(
                privateKeyBytes,
                dataToSign
            )

            acknowledgeMessage(MessageAcknowledgementDTO(
                outputEncryptedMessageDTO.id,
                nonceTimestamp = instant.toLong(),
                phoneNumber = userRepository.getLocalUser().phoneNumber,
                nonce = nonce,
                authMessageSignature = signature
            ))
        }
    }

    private suspend fun createNewContact(decryptedMessage: MessageEntity) {
        val userDTO = withContext(Dispatchers.IO) {
            findUserByPhoneAsync(decryptedMessage.senderPhoneNumber)
        }

        if(userDTO == null) {
            Log.e(
                "SafeChat:MessageSaverService",
                "Contact for phone: ${decryptedMessage.senderPhoneNumber} not found."
            )
            return
        }

        val publicIdentityKey = identityKeyRepository.getIdentityKey(userDTO.phoneNumber).publicKey

        Log.d(
            "SafeChat:MessageSaverService",
            "Contact for phone: ${decryptedMessage.senderPhoneNumber} found.")

        val localUser = userRepository.getLocalUser()

        Log.d(
            "SafeChat:MessageSaverService",
            "Local user: $localUser")

        val newContact = Contact(
            firstName = userDTO.firstName,
            lastName = userDTO.lastName,
            phoneNumber = userDTO.phoneNumber,
            photo = null,
            securityCode = Encoder.encode(publicIdentityKey) + localUser.publicIdentityKey
        )

        contactRepository.createContact(newContact)
        contactListener?.onNewContact(newContact)

        Log.d(
            "SafeChat:MessageSaverService",
            "New contact created: $newContact"
        )
    }

    private suspend fun decryptMessage(outputEncryptedMessageDTO: OutputEncryptedMessageDTO): MessageEntity? {

        val decryptedMessage = messageDecryptor.decryptMessage(outputEncryptedMessageDTO)

        if(decryptedMessage == null) {
            Log.e(
                "SafeChat:MessageSaverService",
                "Failed to decrypt message from: ${outputEncryptedMessageDTO.from}"
            )
            showNotification("Failed to decrypt message from: ${outputEncryptedMessageDTO.from}")
            return null
        } else{
            Log.d(
                "SafeChat:MessageSaverService",
                "Successfully decrypted message from: ${outputEncryptedMessageDTO.from}" +
                        " message: $decryptedMessage")
            showNotification("New message from: ${outputEncryptedMessageDTO.from}")
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        val timestamp = dateFormat.parse(outputEncryptedMessageDTO.date)

        Log.d("SafeChat:MessageSaverService", "Timestamp: $timestamp for message: $decryptedMessage")

        return MessageEntity(
            senderPhoneNumber = outputEncryptedMessageDTO.from,
            receiverPhoneNumber = outputEncryptedMessageDTO.to,
            content = decryptedMessage,
            timestamp = timestamp
        )
    }

    private suspend fun saveNewMessage(decryptedMessage: MessageEntity) {

        val contactFrom = contactRepository.getContact(decryptedMessage.senderPhoneNumber)

        if(contactFrom == null) {
            createNewContact(decryptedMessage)
        }

        Log.d(
            "SafeChat:MessageSaverService",
            "Received new message from: ${decryptedMessage.senderPhoneNumber}" +
                    " message: ${decryptedMessage.content}")

        messageRepository.addMessage(decryptedMessage)
    }

    private suspend fun findUserByPhoneAsync(phoneNumber: String): UserDTO? {
        return withContext(Dispatchers.IO) {

            val userWebService = retrofit.create(UserWebService::class.java)

            try {
                val response: Response<UserDTO> = userWebService.findUserByPhoneNumber(phoneNumber).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                println("Exception ${e.message}")
                null
            }
        }
    }

    private suspend fun acknowledgeMessage(messageAcknowledgementDTO: MessageAcknowledgementDTO) {

        withContext(Dispatchers.IO) {
            val chatWebService = retrofit.create(ChatWebService::class.java)

            try {
                val response : Response<Void> = chatWebService.acknowledge(messageAcknowledgementDTO).execute()
                if(response.isSuccessful) {
                    Log.d("SafeChat:MessageSaverService", "Successfully send message receive acknowledgement.")
                } else {
                    Log.e("SafeChat:MessageSaverService", "Failed to send message receive acknowledgement.")
                }
            } catch (e: Exception) {
                Log.e("SafeChat:MessageSaverService",
                    "Exception while trying to send message receive acknowledgement.")
            }
        }
    }

    private suspend fun getNewMessages(localUserPhoneNumber: String) : List<OutputEncryptedMessageDTO>? {
        return withContext(Dispatchers.IO) {
            val chatWebService = retrofit.create(ChatWebService::class.java)

            try {

                val nonce =  AuthMessageHelper.generateNonce()
                val instant = Instant.now().epochSecond.toString()
                val privateKeyBytes = Decoder.decode(userRepository.getLocalUser().privateIdentityKey)
                val dataToSign = nonce.plus(Decoder.decode(instant))
                val signature = AuthMessageHelper.generateSignature(
                    privateKeyBytes,
                    dataToSign
                )
                val getMessagesDTO = GetMessagesDTO(
                    phoneNumber = localUserPhoneNumber,
                    nonce = nonce,
                    nonceTimestamp = instant.toLong(),
                    authMessageSignature = signature
                )

                val response : Response<List<OutputEncryptedMessageDTO>> =
                    chatWebService.getNewMessages(getMessagesDTO).execute()
                if(response.isSuccessful) {
                    Log.d("SafeChat:MessageSaverService", "Successfully fetched new messages. Response: ${response.body()}")
                    response.body()
                } else {
                    Log.e("SafeChat:MessageSaverService", "Failed to fetch new messages." +
                            " Message: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("SafeChat:MessageSaverService",
                    "Exception while trying to fetch new messages. ${e.message}")
                null
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null // We don't need binding, so returning null
    }

    private var notificationId = 0

    private fun showNotification(message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "safechat_notifications"
        val channelName = "SafeChat Notifications"

        // Create notification channel for Android O+
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Message")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        notificationManager.notify(notificationId, notification)

        notificationId++
    }
}