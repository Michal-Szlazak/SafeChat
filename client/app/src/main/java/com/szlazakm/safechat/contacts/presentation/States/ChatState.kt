package com.szlazakm.safechat.contacts.presentation.States

import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.Message
import java.util.Date

data class ChatState(
    val messages: List<Message.TextMessage> = emptyList(),
    val selectedContact: Contact? = null,
    val isTyping: Boolean = false
)