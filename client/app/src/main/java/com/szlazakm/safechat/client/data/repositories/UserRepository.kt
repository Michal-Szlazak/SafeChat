package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.UserEntity
import javax.inject.Inject

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

    fun getLocalUser() : UserEntity? {
        return database.userDao().getUser()
    }
}