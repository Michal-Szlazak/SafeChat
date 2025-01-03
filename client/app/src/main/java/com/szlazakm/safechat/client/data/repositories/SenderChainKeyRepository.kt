package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.SenderChainKeyEntity
import javax.inject.Inject

class SenderChainKeyRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun createChainKey(senderChainKeyEntity: SenderChainKeyEntity) {
        database.senderChainKeyDao().insertChainKey(senderChainKeyEntity)
    }

    fun updateChainKey(senderChainKeyEntity: SenderChainKeyEntity) {
        database.senderChainKeyDao().updateChainKey(senderChainKeyEntity)
    }
}