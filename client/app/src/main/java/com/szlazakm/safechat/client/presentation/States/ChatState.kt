package com.szlazakm.safechat.client.presentation.States

import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.Message

data class ChatState(
    val messages: List<Message.TextMessage> = emptyList(),
    val selectedContact: Contact? = null,
    val isTyping: Boolean = false
)