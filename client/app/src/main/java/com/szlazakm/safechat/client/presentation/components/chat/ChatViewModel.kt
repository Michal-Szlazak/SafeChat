package com.szlazakm.safechat.client.presentation.components.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.szlazakm.safechat.client.data.Entities.MessageEntity
import com.szlazakm.safechat.client.data.Entities.UserEntity
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.data.Repositories.MessageRepository
import com.szlazakm.safechat.client.data.Repositories.UserRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.Message
import com.szlazakm.safechat.client.presentation.Events.ChatEvent
import com.szlazakm.safechat.client.presentation.States.ChatState
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import com.szlazakm.safechat.webclient.dtos.MessageSentResponseDTO
import com.szlazakm.safechat.webclient.dtos.OutputMessageDTO
import com.szlazakm.safechat.webclient.webservices.ChatWebService
import com.szlazakm.safechat.client.data.services.StompService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    private val retrofit: Retrofit
): ViewModel() {

    private val chatState: MutableStateFlow<ChatState> = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = chatState
    private val stompService: StompService = StompService()

    private val _contact = MutableLiveData<Contact>()
    val contact: LiveData<Contact> = _contact

    private val _localUserEntity = MutableLiveData<UserEntity?>()
    val localUserEntity: MutableLiveData<UserEntity?> = _localUserEntity

    private val chatWebService = retrofit.create(ChatWebService::class.java)

    fun setContact(contact: Contact) {
        _contact.value = contact
    }

    fun getLocalUserEntity() : UserEntity {
        return localUserEntity.value!!
    }

    fun loadChat() {

        viewModelScope.launch {

            val localUserEntity = withContext(Dispatchers.IO) {
                userRepository.getLocalUser()
            }

            if(localUserEntity == null) {
                Log.e(
                    "ChatViewModel",
                    "Failed to load chat, local user not present."
                )
                return@launch
            }

            _localUserEntity.value = localUserEntity

            val messages = withContext(Dispatchers.IO) {

                stompService.disconnect()
                stompService.connect()
                stompService.subscribeToTopic("/user/queue/${localUserEntity.phoneNumber}") {
                    message -> println("Received in ChatViewModel: $message")
                    val gson = Gson()
                    val output = gson.fromJson(message, OutputMessageDTO::class.java)

                    if(output.to == localUserEntity.phoneNumber && output.from == contact.value?.phoneNumber ?: "") {

//                    val dateFormat = getDateTimeInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val timestamp = dateFormat.parse(output.date)

                        Log.d("Debug message date", "The output ${output.date}")

                        val textMessage = Message.TextMessage(
                            senderPhoneNumber = output.from,
                            receiverPhoneNumber = output.to,
                            content = output.text,
                            timestamp = timestamp
                        )

                        val updatedMessages = state.value.messages.toMutableList().apply {
                            add(textMessage)
                        }
                        chatState.value = state.value.copy(messages = updatedMessages)
                    }

                }

                val from = localUserEntity.phoneNumber
                val to = _contact.value?.phoneNumber ?: return@withContext emptyList<Message.TextMessage>()

                messageRepository.getMessages(
                    from,
                    to
                )
            }

            withContext(Dispatchers.IO) {
                val from = localUserEntity.phoneNumber
                val to = _contact.value?.phoneNumber ?: return@withContext emptyList<Message.TextMessage>()
                val messageList = messageRepository.getMessages(from, to)

                messageList.forEach{
                    message -> Log.d("ChatViewModel", "Found message $message")
                }
            }

            chatState.value = chatState.value.copy(
                messages = messages
            )

        }
    }

    fun getContact(phoneNumber: String) : Contact? {
        return contactRepository.getContact(phoneNumber)
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.MessageReceived -> {
                val updatedMessages = chatState.value.messages + event.message
                chatState.value = chatState.value.copy(messages = updatedMessages)
            }
            is ChatEvent.SendMessage -> {

                viewModelScope.launch {

                    val selectedContact = contact.value?.phoneNumber ?: run {
                        Log.e("ERROR", "Selected contact is null.")
                        return@launch  // Exit the coroutine early if contact.value is null
                    }

                    val localUser = withContext(Dispatchers.IO) {
                        userRepository.getLocalUser()
                    }

                    if(localUser == null) {
                        Log.e(
                            "ChatViewModel",
                            "Failed to load chat, local user not present."
                        )
                        return@launch
                    }

                    val messageDTO = MessageDTO(
                        from = localUser.phoneNumber,
                        to = selectedContact,
                        text = event.message
                    )

                    withContext(Dispatchers.IO) {
                        try {

                            val response : Response<MessageSentResponseDTO> = chatWebService.sendMessage(messageDTO).execute()

                            println(response)
                            if (response.isSuccessful) {
                                println("Message send succesfuly. Response code: ${response.code()}")

                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val messageSentResponseDTO = response.body()
                                val timestamp = dateFormat.parse(messageSentResponseDTO?.timestamp)

                                val message = Message.TextMessage(
                                    content = event.message,
                                    senderPhoneNumber = localUser.phoneNumber,
                                    receiverPhoneNumber = selectedContact,
                                    timestamp
                                )
                                val updatedMessages = state.value.messages.toMutableList().apply {
                                    add(message)
                                }

                                (Dispatchers.IO) {
                                    messageRepository.addMessage(
                                        MessageEntity(
                                            content = event.message,
                                            senderPhoneNumber = localUser.phoneNumber,
                                            receiverPhoneNumber = selectedContact,
                                            timestamp = timestamp
                                        )
                                    )
                                }

                                chatState.value = state.value.copy(messages = updatedMessages)
                            } else {
                                println("Failed send message. Response code: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            println("Failed to send message: ${e.message}")
                        }
                    }


                }

            }
            is ChatEvent.StartTyping -> {
                chatState.value = chatState.value.copy(isTyping = true)
            }
            is ChatEvent.StopTyping -> {
                chatState.value = chatState.value.copy(isTyping = false)
            }
        }
    }
}