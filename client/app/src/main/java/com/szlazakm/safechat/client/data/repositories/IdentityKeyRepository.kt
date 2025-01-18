package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.IdentityKeyEntity
import javax.inject.Inject

class IdentityKeyRepository  @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun createIdentityKey(identityKeyEntity: IdentityKeyEntity) {
        database.identityKeyDao().createIdentityKey(identityKeyEntity)
    }

    fun getIdentityKey(phoneNumber: String): IdentityKeyEntity {
        return database.identityKeyDao().getIdentityKey(phoneNumber)
    }
}