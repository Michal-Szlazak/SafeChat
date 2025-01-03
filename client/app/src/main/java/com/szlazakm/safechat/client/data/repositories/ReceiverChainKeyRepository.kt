package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.ReceiverChainKeyEntity
import javax.inject.Inject

class ReceiverChainKeyRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun createChainKey(receiverChainKeyEntity: ReceiverChainKeyEntity) {
        database.receiverChainKeyDao().insertChainKey(receiverChainKeyEntity)
    }

    fun updateChainKey(receiverChainKeyEntity: ReceiverChainKeyEntity) {
        database.receiverChainKeyDao().updateChainKey(receiverChainKeyEntity)
    }
}