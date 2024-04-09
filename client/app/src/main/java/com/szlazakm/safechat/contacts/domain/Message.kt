package com.szlazakm.safechat.contacts.domain

import java.util.Date
import java.util.UUID

sealed class Message {
    data class TextMessage(val content: String, val senderId: UUID, val timestamp: Date) : Message()
    // Add more message types like images, files, etc. if needed
}