package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.entities.MessageKeysEntity

@Dao
interface MessageKeysDao {

    @Insert
    fun createMessageKeys(messageKeysEntity: MessageKeysEntity)

    @Delete
    fun deleteMessageKeys(messageKeysEntity: MessageKeysEntity)

    @Query("SELECT * FROM message_keys_entity WHERE phoneNumber = :phoneNumber AND ephemeralRatchetKey = :ephemeralRatchetKey")
    fun getMessageKeys(phoneNumber: String, ephemeralRatchetKey: ByteArray): List<MessageKeysEntity>
}