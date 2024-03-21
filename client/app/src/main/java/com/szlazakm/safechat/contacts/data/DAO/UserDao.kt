package com.szlazakm.safechat.contacts.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.contacts.data.Entities.UserEntity

@Dao
interface UserDao {

    @Insert
    fun insertUser(contact: UserEntity)

    @Query("SELECT * FROM user_entity WHERE phoneNumber = :phoneNumber LIMIT 1")
    fun getUser(phoneNumber: String): UserEntity?

    @Query("SELECT COUNT(*) FROM user_entity")
    fun isUserCreated(): Boolean

    @Query("DELETE FROM user_entity")
    fun clearUserDB()
}