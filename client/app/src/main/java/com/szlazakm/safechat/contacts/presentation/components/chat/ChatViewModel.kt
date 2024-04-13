package com.szlazakm.safechat.contacts.presentation.components.chat

import android.util.Log
import androidx.compose.runtime.sourceInformation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.szlazakm.safechat.contacts.data.Entities.UserEntity
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import com.szlazakm.safechat.contacts.data.Repositories.MessageRepository
import com.szlazakm.safechat.contacts.data.Repositories.UserRepository
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.Message
import com.szlazakm.safechat.contacts.presentation.Events.ChatEvent
import com.szlazakm.safechat.contacts.presentation.States.ChatState
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import com.szlazakm.safechat.webclient.dtos.OutputMessageDTO
import com.szlazakm.safechat.webclient.services.StompService
import com.szlazakm.safechat.webclient.services.WebSocketListenerImpl
import com.szlazakm.safechat.webclient.services.connectWebSocket
import com.szlazakm.safechat.webclient.services.sendMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.WebSocketListener
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import java.util.logging.Logger
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val chatState: MutableStateFlow<ChatState> = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = chatState
    private val stompService: StompService = StompService("ws://192.168.0.230:8080/ws")

    private val _contact = MutableLiveData<Contact>()
    val contact: LiveData<Contact> = _contact

    private val _localUserEntity = MutableLiveData<UserEntity?>()
    val localUserEntity: MutableLiveData<UserEntity?> = _localUserEntity

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

                    stompService.sendMessage("/app/room", messageDTO)

                    val updatedMessages = state.value.messages.toMutableList().apply {
                        add(
                            Message.TextMessage(
                                content = event.message,
                                senderPhoneNumber = localUser.phoneNumber,
                                receiverPhoneNumber = selectedContact,
                                Date()
                            )
                        )
                    }
                    chatState.value = state.value.copy(messages = updatedMessages)
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