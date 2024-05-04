package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.entities.UserEntity

@Dao
interface UserDao {

    @Insert
    fun insertUser(contact: UserEntity)

    @Query("SELECT * FROM user_entity LIMIT 1")
    fun getUser(): UserEntity?

    @Query("SELECT COUNT(*) FROM user_entity")
    fun isUserCreated(): Boolean

    @Query("DELETE FROM user_entity")
    fun clearUserDB()
}