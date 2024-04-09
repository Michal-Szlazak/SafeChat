package com.szlazakm.safechat.contacts.presentation.components.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import com.szlazakm.safechat.contacts.data.Repositories.MessageRepository
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.Message
import com.szlazakm.safechat.contacts.presentation.Events.ChatEvent
import com.szlazakm.safechat.contacts.presentation.States.ChatState
import com.szlazakm.safechat.webclient.dtos.MessageDTO
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
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository
): ViewModel() {
    private val chatState: MutableStateFlow<ChatState> =
        MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = chatState
    private val webSocketConnection = connectWebSocket()

    init {

        viewModelScope.launch {
            // Fetch contacts and recent contacts from the messageRepository
            withContext(Dispatchers.IO) {
                messageRepository.insertHardcodedMessages()
            }

            val messages = withContext(Dispatchers.IO) {
                messageRepository.getMessages()
            }

            // Update the state flow with the fetched data
            chatState.value = chatState.value.copy(
                messages = messages
            )

        }
    }

    fun getContact(userId: UUID) : Contact? {
        return contactRepository.getContact(userId)
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.MessageReceived -> {
                val updatedMessages = chatState.value.messages + event.message
                chatState.value = chatState.value.copy(messages = updatedMessages)
            }
            is ChatEvent.SendMessage -> {

                val messageDTO = MessageDTO(
                    from = "from",
                    to = state.value.selectedContact?.id.toString(),
                    text = event.message
                )

                sendMessage(webSocketConnection, messageDTO)

                val updatedMessages = state.value.messages.toMutableList().apply {
                    add(
                        Message.TextMessage(
                            content = event.message,
                            senderId = UUID.randomUUID(),
                            Date()
                        )
                    )
                }
                chatState.value = state.value.copy(messages = updatedMessages)
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