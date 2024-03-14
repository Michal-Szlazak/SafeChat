package com.szlazakm.safechat.contacts.data.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.szlazakm.safechat.contacts.domain.Message
import java.util.Date

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
    val senderId: Long,
    @ColumnInfo(index = true)
    val receiverId: Long,
    val timestamp: Date
)

fun MessageEntity.toTextMessage(): Message.TextMessage {
    return Message.TextMessage(
        content = this.content,
        senderId = this.senderId,
        timestamp = this.timestamp
    )
}