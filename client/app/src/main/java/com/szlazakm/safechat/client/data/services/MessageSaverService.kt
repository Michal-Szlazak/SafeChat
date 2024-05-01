package com.szlazakm.safechat.client.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.szlazakm.safechat.client.data.Entities.MessageEntity
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.data.Repositories.MessageRepository
import com.szlazakm.safechat.client.data.Repositories.UserRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.webclient.dtos.MessageAcknowledgementDTO
import com.szlazakm.safechat.webclient.dtos.OutputMessageDTO
import com.szlazakm.safechat.webclient.dtos.UserDTO
import com.szlazakm.safechat.webclient.webservices.ChatWebService
import com.szlazakm.safechat.webclient.webservices.UserWebService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

    private val stompService: StompService = StompService()
    private val gson: Gson = Gson()
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MessageSaverService", "onStart called")
        serviceScope.launch{
            connectToUserQueue()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MessageSaverService", "onDestroy called")
    }

    private suspend fun connectToUserQueue() {

        val localUser = userRepository.getLocalUser()

        if(localUser == null) {
            Log.e(
                "MessageSaverService",
                "LocalUser is not created yet. Aborting MessageServiceCreation."
            )
            return
        } else {
            Log.e(
                "MessageSaverService",
                "LocalUser is found. Loading MessageServiceCreation."
            )
        }


        val newMessages = getNewMessages(localUser.phoneNumber)

        newMessages?.forEach{
            withContext(Dispatchers.IO) {
                saveNewMessage(it)
            }
        }


        stompService.connect()
        stompService.subscribeToTopic("/user/queue/${localUser.phoneNumber}") { message ->

            Log.d("MessageSaverService","received a message $message")

            serviceScope.launch {

                val outputMessageDTO = gson.fromJson(message, OutputMessageDTO::class.java)

                saveNewMessage(outputMessageDTO)
                acknowledgeMessage(MessageAcknowledgementDTO(outputMessageDTO.id))
            }

        }
    }

    private suspend fun createNewContact(outputMessageDTO: OutputMessageDTO) {
        val userDTO = withContext(Dispatchers.IO) {
            findUserByPhoneAsync(outputMessageDTO.from)
        }

        if(userDTO == null) {
            Log.e(
                "MessageSaverService",
                "Contact for phone: ${outputMessageDTO.from} not found."
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

        Log.d(
            "MessageSaverService",
            "New contact created: $newContact"
        )
    }

    private suspend fun saveNewMessage(outputMessageDTO: OutputMessageDTO) {

        val contactFrom = contactRepository.getContact(outputMessageDTO.from)

        if(contactFrom == null) {
            createNewContact(outputMessageDTO)
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = dateFormat.parse(outputMessageDTO.date)

        val messageEntity = MessageEntity(
            content = outputMessageDTO.text,
            senderPhoneNumber = outputMessageDTO.from,
            receiverPhoneNumber = outputMessageDTO.to,
            timestamp = timestamp
        )

        messageRepository.addMessage(messageEntity)
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
                if(!response.isSuccessful) {
                    Log.e("MessageSaverService", "Failed to send message receive acknowledgement.")
                } else {
                    Log.e("MessageSaverService", "Successfully to send message receive acknowledgement.")
                }
            } catch (e: Exception) {
                Log.e("MessageSaverService",
                    "Exception while trying to send message receive acknowledgement.")
            }
        }
    }

    private suspend fun getNewMessages(localUserPhoneNumber: String) : List<OutputMessageDTO>? {
        return withContext(Dispatchers.IO) {
            val chatWebService = retrofit.create(ChatWebService::class.java)

            try {
                val response : Response<List<OutputMessageDTO>> =
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
        TODO("Not yet implemented")
        return null // We don't need binding, so returning null
    }
}