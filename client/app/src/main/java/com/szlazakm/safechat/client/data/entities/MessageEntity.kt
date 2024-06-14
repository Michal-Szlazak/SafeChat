package com.szlazakm.safechat.client.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.szlazakm.safechat.client.domain.Message
import java.util.Date

@Entity(tableName = "message_entity",
    foreignKeys = [
        ForeignKey(entity = ContactEntity::class,
            parentColumns = ["phoneNumber"],
            childColumns = ["senderPhoneNumber"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ContactEntity::class,
            parentColumns = ["phoneNumber"],
            childColumns = ["receiverPhoneNumber"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    @ColumnInfo(index = true)
    val senderPhoneNumber: String,
    @ColumnInfo(index = true)
    val receiverPhoneNumber: String,
    val timestamp: Date
)

fun MessageEntity.toTextMessage(): Message.TextMessage {
    return Message.TextMessage(
        content = this.content,
        senderPhoneNumber = this.senderPhoneNumber,
        receiverPhoneNumber = this.receiverPhoneNumber,
        timestamp = this.timestamp
    )
}