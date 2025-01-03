package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.EncryptionSession
import com.szlazakm.safechat.client.data.entities.RootKeyEntity
import javax.inject.Inject

class RootKeyRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun createRootKey(rootKeyEntity: RootKeyEntity) {
        val existentRootKeys = database.rootKeyDao().getRootKeys()

        Log.d("RootKeyRepository", "listing root keys")
        for (rootKey in existentRootKeys) {
            Log.d("RootKeyRepository", "existent root key: $rootKey")
        }

        Log.d("RootKeyRepository", "creating root key $rootKeyEntity")
        database.rootKeyDao().insertRootKey(rootKeyEntity)
    }

    fun getEncryptionSession(phoneNumber: String): EncryptionSession? {
        return database.rootKeyDao().getEncryptionSession(phoneNumber)
    }

    fun updateRootKey(rootKeyEntity: RootKeyEntity) {
        Log.d("RootKeyRepository", "updating root key $rootKeyEntity")
        database.rootKeyDao().updateRootKey(rootKeyEntity)
    }
}