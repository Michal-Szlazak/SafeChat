package com.szlazakm.safechat.webclient.services

import android.util.Log
import com.google.gson.Gson
import com.szlazakm.safechat.contacts.data.Entities.MessageEntity
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import com.szlazakm.safechat.contacts.data.Repositories.MessageRepository
import com.szlazakm.safechat.contacts.data.Repositories.UserRepository
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.webclient.dtos.OutputMessageDTO
import com.szlazakm.safechat.webclient.dtos.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import javax.inject.Inject

class MessageSaverService @Inject constructor(
    val userService: UserService,
    val contactRepository: ContactRepository,
    val messageRepository: MessageRepository,
    val userRepository: UserRepository
){

    private val stompService: StompService = StompService("ws://192.168.0.230:8080/ws")
    private val gson: Gson = Gson()

    fun connectToUserQueue() {

        val localUser = userRepository.getLocalUser()

        if(localUser == null) {
            Log.e(
                "MessageSaverService",
                "LocalUser is not created yet. Aborting MessageServiceCreation."
            )
            return
        }

        stompService.connect()
        stompService.subscribeToTopic("/user/queue/${localUser.phoneNumber}") { message ->

            GlobalScope.launch {

                val outputMessageDTO = gson.fromJson(message, OutputMessageDTO::class.java)
                val contactFrom = contactRepository.getContact(outputMessageDTO.from)

                if(contactFrom == null) {

                    val userDTO = findUserByPhoneAsync(outputMessageDTO.from)

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
                        email = "",
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

                val message = MessageEntity(
                    content = outputMessageDTO.text,
                    senderPhoneNumber = outputMessageDTO.from,
                    receiverPhoneNumber = outputMessageDTO.to,
                    timestamp = timestamp
                )

                messageRepository.addMessage(message)
            }

        }
    }

    private suspend fun findUserByPhoneAsync(phoneNumber: String): UserDTO? {
        return withContext(Dispatchers.IO) {

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