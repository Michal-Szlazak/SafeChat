package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.MessageKeysEntity
import javax.inject.Inject

class MessageKeysRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun createMessageKeys(messageKeysEntity: MessageKeysEntity) {
        database.messageKeysDao().createMessageKeys(messageKeysEntity)
    }

    fun deleteMessageKeysDao(messageKeysEntity: MessageKeysEntity) {
        database.messageKeysDao().deleteMessageKeys(messageKeysEntity)
    }

    fun getMessageKeys(phoneNumber: String, ephemeralRatchetKey: ByteArray): List<MessageKeysEntity> {
        return database.messageKeysDao().getMessageKeys(phoneNumber, ephemeralRatchetKey)
    }
}