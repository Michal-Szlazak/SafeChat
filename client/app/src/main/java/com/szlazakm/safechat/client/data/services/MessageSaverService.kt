package com.szlazakm.safechat.webclient.services

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.szlazakm.safechat.client.data.Entities.MessageEntity
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.data.Repositories.MessageRepository
import com.szlazakm.safechat.client.data.Repositories.UserRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.webclient.dtos.OutputMessageDTO
import com.szlazakm.safechat.webclient.dtos.UserDTO
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import javax.inject.Inject

class MessageSaverService @Inject constructor(
    private val contactRepository: ContactRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val retrofit: Retrofit
) : ViewModel(){

    private val stompService: StompService = StompService()
    private val gson: Gson = Gson()

    @OptIn(DelicateCoroutinesApi::class)
    fun connectToUserQueue() {

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

        stompService.connect()
        stompService.subscribeToTopic("/user/queue/${localUser.phoneNumber}") { message ->

            Log.d("MessageSaverService","received a message $message")

            GlobalScope.launch {

                val outputMessageDTO = gson.fromJson(message, OutputMessageDTO::class.java)
                val contactFrom = contactRepository.getContact(outputMessageDTO.from)

                if(contactFrom == null) {

                    val userDTO = withContext(Dispatchers.IO) {
                        println(findUserByPhoneAsync(outputMessageDTO.from))
                        findUserByPhoneAsync(outputMessageDTO.from)
                    }

                    if(userDTO == null) {
                        Log.e(
                            "MessageSaverService",
                            "Contact for phone: ${outputMessageDTO.from} not found."
                        )
                        return@launch
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

        }
    }

    private suspend fun findUserByPhoneAsync(phoneNumber: String): UserDTO? {
        return withContext(Dispatchers.IO) {

            val userService = retrofit.create(UserService::class.java)

            try {
                val response: Response<UserDTO> = userService.findUserByPhoneNumber(phoneNumber).execute()
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
}