package com.szlazakm.safechat.contacts.data.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.szlazakm.safechat.contacts.domain.Message
import java.util.Date
import java.util.UUID

@Entity(tableName = "message_entity",
    foreignKeys = [
        ForeignKey(entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    @ColumnInfo(index = true)
    val senderId: UUID,
    @ColumnInfo(index = true)
    val receiverId: UUID,
    val timestamp: Date
)

fun MessageEntity.toTextMessage(): Message.TextMessage {
    return Message.TextMessage(
        content = this.content,
        senderId = this.senderId,
        timestamp = this.timestamp
    )
}