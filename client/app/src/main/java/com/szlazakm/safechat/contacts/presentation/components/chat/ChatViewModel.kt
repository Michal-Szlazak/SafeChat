package com.szlazakm.safechat.contacts.presentation.components.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import com.szlazakm.safechat.contacts.data.Repositories.MessageRepository
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.Message
import com.szlazakm.safechat.contacts.presentation.Events.ChatEvent
import com.szlazakm.safechat.contacts.presentation.States.ChatState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository
): ViewModel() {
    private val chatState: MutableStateFlow<ChatState> =
        MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = chatState

    init {
        println("hello there in chat")
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

    fun getContact(userId: Long) : Contact? {
        return contactRepository.getContact(userId)
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.MessageReceived -> {
                val updatedMessages = chatState.value.messages + event.message
                chatState.value = chatState.value.copy(messages = updatedMessages)
            }
            is ChatEvent.SendMessage -> {
                val updatedMessages = state.value.messages.toMutableList().apply {
                    add(
                        Message.TextMessage(
                            content = event.message,
                            senderId = 1,
                            Date()
                        )
                    )
                }
                chatState.value = state.value.copy(messages = updatedMessages)
            }
            ChatEvent.StartTyping -> {
                chatState.value = chatState.value.copy(isTyping = true)
            }
            ChatEvent.StopTyping -> {
                chatState.value = chatState.value.copy(isTyping = false)
            }
        }
    }
}