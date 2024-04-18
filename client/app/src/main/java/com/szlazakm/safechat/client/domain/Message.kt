package com.szlazakm.safechat.client.domain

import java.util.Date

sealed class Message {
    data class TextMessage(
        val content: String,
        val senderPhoneNumber: String,
        val receiverPhoneNumber: String,
        val timestamp: Date
    ) : Message()
}