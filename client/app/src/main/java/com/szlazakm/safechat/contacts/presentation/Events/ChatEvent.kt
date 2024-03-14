package com.szlazakm.safechat.contacts.presentation.Events

import com.szlazakm.safechat.contacts.domain.Message


sealed interface ChatEvent {
    data class MessageReceived(val message: Message.TextMessage) : ChatEvent
    data class SendMessage(val message: String) : ChatEvent
    object StartTyping : ChatEvent
    object StopTyping : ChatEvent
}