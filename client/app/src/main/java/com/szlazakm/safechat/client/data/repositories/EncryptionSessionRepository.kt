package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.EncryptionSessionEntity
import javax.inject.Inject


class EncryptionSessionRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun getEncryptionSessionByPhoneNumber(phoneNumber: String) : EncryptionSessionEntity? {
        return database.encryptionSessionDao().getEncryptionSessionByPhoneNumber(phoneNumber)
    }

    fun deleteEncryptionSessionByPhoneNumber(phoneNumber: String) {
        database.encryptionSessionDao().deleteEncryptionSessionByPhoneNumber(phoneNumber)
    }

    fun createNewEncryptionSession(encryptionSessionEntity: EncryptionSessionEntity) {
        database.encryptionSessionDao().createEncryptionSession(encryptionSessionEntity)
    }
}