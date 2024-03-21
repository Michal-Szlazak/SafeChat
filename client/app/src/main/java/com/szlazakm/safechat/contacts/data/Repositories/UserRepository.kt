package com.szlazakm.safechat.contacts.data.Repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.contacts.data.Entities.UserEntity
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

    fun clearUserDB() {

        database.userDao().clearUserDB()
    }
}