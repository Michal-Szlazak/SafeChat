package com.szlazakm.safechat.client.data.Repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.Entities.UserEntity
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper
import javax.inject.Inject
import kotlin.random.Random

class UserRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun isUserCreated(): Boolean {
        return database.userDao().isUserCreated()
    }

    fun createUser(userEntity: UserEntity) {
        database.userDao().insertUser(userEntity)
    }

    fun clearUserDB() {
        database.userDao().clearUserDB()
    }

    fun getLocalUser() : UserEntity? {
        return database.userDao().getUser()
    }
}