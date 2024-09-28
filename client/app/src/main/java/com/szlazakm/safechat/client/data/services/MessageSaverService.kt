package com.szlazakm.safechat.client.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.szlazakm.safechat.client.data.entities.MessageEntity
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.MessageRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.utils.auth.MessageDecryptor
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

    private val stompService: StompService = StompService()
    private val gson: Gson = Gson()
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    private var messageListener: MessageListener? = null
    private var contactListener: ContactListener? = null

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
        Log.d("MessageSaverService", "onStart called")
        serviceScope.launch{
            connectToUserQueue()
        }

        return START_STICKY
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
        Log.d("MessageSaverService", "onCreate called")
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
        Log.d("MessageSaverService", "onDestroy called")
    }

    private suspend fun connectToUserQueue() {

        var localUser: UserEntity

        try {
            localUser = userRepository.getLocalUser()
        } catch (e: Exception) {
            Log.e(
                "MessageSaverService",
                "Exception while trying to get local user. Aborting MessageServiceCreation."
            )
            return
        }


        val newMessages = getNewMessages(localUser.phoneNumber)
        loadNewMessagesFromDB(newMessages ?: emptyList())

        stompService.connect()
        stompService.subscribeToTopic("/user/queue/${localUser.phoneNumber}") { message ->

            Log.d("MessageSaverService","received a message $message")

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
                        "MessageSaverService",
                        "Failed to decrypt message from: ${it.from}"
                    )
                    return@withContext
                } else{
                    Log.d(
                        "MessageSaverService",
                        "Successfully decrypted message from: ${it.from}" +
                                " message: $decryptedMessage")
                }

                saveNewMessage(decryptedMessage)
            }
        }
    }

    private suspend fun handleNewMessage(outputEncryptedMessageDTO: OutputEncryptedMessageDTO) {
        val decryptedMessage = decryptMessage(outputEncryptedMessageDTO)

        if(decryptedMessage == null) {
            Log.e(
                "MessageSaverService",
                "Failed to decrypt message from: ${outputEncryptedMessageDTO.from}"
            )
            return
        } else{
            Log.d(
                "MessageSaverService",
                "Successfully decrypted message from: ${outputEncryptedMessageDTO.from}" +
                        " message: $decryptedMessage")
        }

        messageListener?.onNewMessage(decryptedMessage)

        saveNewMessage(decryptedMessage)
        acknowledgeMessage(MessageAcknowledgementDTO(outputEncryptedMessageDTO.id))
    }

    private suspend fun createNewContact(decryptedMessage: MessageEntity) {
        val userDTO = withContext(Dispatchers.IO) {
            findUserByPhoneAsync(decryptedMessage.senderPhoneNumber)
        }

        if(userDTO == null) {
            Log.e(
                "MessageSaverService",
                "Contact for phone: ${decryptedMessage.senderPhoneNumber} not found."
            )
            return
        }

        val newContact = Contact(
            firstName = userDTO.firstName,
            lastName = userDTO.lastName,
            phoneNumber = userDTO.phoneNumber,
            photo = null
        )

        contactRepository.createContact(newContact)
        contactListener?.onNewContact(newContact)

        Log.d(
            "MessageSaverService",
            "New contact created: $newContact"
        )
    }

    private suspend fun decryptMessage(outputEncryptedMessageDTO: OutputEncryptedMessageDTO): MessageEntity? {

        val decryptedMessage = messageDecryptor.decryptMessage(outputEncryptedMessageDTO)

        if(decryptedMessage == null) {
            Log.e(
                "MessageSaverService",
                "Failed to decrypt message from: ${outputEncryptedMessageDTO.from}"
            )
            return null
        } else{
            Log.d(
                "MessageSaverService",
                "Successfully decrypted message from: ${outputEncryptedMessageDTO.from}" +
                        " message: $decryptedMessage")
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = dateFormat.parse(outputEncryptedMessageDTO.date)

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
            "MessageSaverService",
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
                    Log.d("MessageSaverService", "Successfully to send message receive acknowledgement.")
                } else {
                    Log.e("MessageSaverService", "Failed to send message receive acknowledgement.")
                }
            } catch (e: Exception) {
                Log.e("MessageSaverService",
                    "Exception while trying to send message receive acknowledgement.")
            }
        }
    }

    private suspend fun getNewMessages(localUserPhoneNumber: String) : List<OutputEncryptedMessageDTO>? {
        return withContext(Dispatchers.IO) {
            val chatWebService = retrofit.create(ChatWebService::class.java)

            try {
                val response : Response<List<OutputEncryptedMessageDTO>> =
                    chatWebService.getNewMessages(localUserPhoneNumber).execute()
                if(response.isSuccessful) {
                    Log.d("MessageSaverService", "Successfully fetched new messages.")
                    response.body()
                } else {
                    Log.e("MessageSaverService", "Failed to fetch new messages.")
                    null
                }
            } catch (e: Exception) {
                Log.e("MessageSaverService",
                    "Exception while trying to fetch new messages. ${e.message}")
                null
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null // We don't need binding, so returning null
    }
}