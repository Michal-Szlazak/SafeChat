package com.szlazakm.safechat.client.presentation.Events

import com.szlazakm.safechat.client.domain.Message


sealed interface ChatEvent {
    data class MessageReceived(val message: Message.TextMessage) : ChatEvent
    data class SendMessage(val message: String) : ChatEvent
    object StartTyping : ChatEvent
    object StopTyping : ChatEvent
}