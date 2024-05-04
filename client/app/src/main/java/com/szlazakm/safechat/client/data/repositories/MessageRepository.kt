package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.MessageEntity
import com.szlazakm.safechat.client.data.entities.toTextMessage
import com.szlazakm.safechat.client.domain.Message
import javax.inject.Inject

class MessageRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun getMessages(senderPhoneNumber: String, receiverPhoneNumber: String): List<Message.TextMessage> {
        return database.messageDao().getMessages(
            senderPhoneNumber,
            receiverPhoneNumber
        ).map { messageEntity -> messageEntity.toTextMessage() }
    }

    fun addMessage(message: MessageEntity) {
        database.messageDao().insertMessage(message)
    }

}