package com.szlazakm.safechat.client.presentation.components.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.entities.MessageEntity
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.MessageRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.MessageListener
import com.szlazakm.safechat.client.data.services.MessageSaverService
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.Message
import com.szlazakm.safechat.client.presentation.events.ChatEvent
import com.szlazakm.safechat.client.presentation.states.ChatState
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import com.szlazakm.safechat.webclient.dtos.MessageSentResponseDTO
import com.szlazakm.safechat.webclient.webservices.ChatWebService
import com.szlazakm.safechat.utils.auth.EncryptedMessageSender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val retrofit: Retrofit,
    private val encryptedMessageSender: EncryptedMessageSender
): ViewModel(), MessageListener {

    private val chatState: MutableStateFlow<ChatState> = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = chatState

    private val chatWebService = retrofit.create(ChatWebService::class.java)

    fun setContact(contact: Contact) {
        chatState.value = chatState.value.copy(selectedContact = contact)
    }

    fun loadChat() {

        val messageSaverService = MessageSaverService.getInstance()
        messageSaverService?.setMessageListener(this)

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

            val messages = withContext(Dispatchers.IO) {

                val from = localUserEntity.phoneNumber
                val to = chatState.value.selectedContact?.phoneNumber ?: return@withContext emptyList<Message.TextMessage>()
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

                    val selectedContact = chatState.value.selectedContact?.phoneNumber ?: run {
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
                            val encryptedMessage = encryptedMessageSender.encryptMessage(messageDTO)
                            if(encryptedMessage == null) {
                                Log.e("ChatViewModel", "EncryptedMessage is null. Sending aborted")
                                return@withContext
                            }

                            val response : Response<MessageSentResponseDTO> = chatWebService
                                .sendMessage(encryptedMessage).execute()


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


                                messageRepository.addMessage(
                                    MessageEntity(
                                        content = event.message,
                                        senderPhoneNumber = localUser.phoneNumber,
                                        receiverPhoneNumber = selectedContact,
                                        timestamp = timestamp
                                    )
                                )


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

    override fun onNewMessage(message: MessageEntity) {

        Log.d("ChatViewModel", "Received new message via listener: $message")

        if(state.value.selectedContact == null) {
            return
        }

        if(message.senderPhoneNumber != state.value.selectedContact!!.phoneNumber) {
            return
        }

        val textMessage = Message.TextMessage(
            content = message.content,
            senderPhoneNumber = message.senderPhoneNumber,
            receiverPhoneNumber = message.receiverPhoneNumber,
            timestamp = message.timestamp
        )

        val updatedMessages = state.value.messages.toMutableList().apply {
            add(textMessage)
        }

        chatState.value = state.value.copy(messages = updatedMessages)
    }
}