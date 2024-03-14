package com.szlazakm.safechat.contacts.data.DAO;

import androidx.room.Dao;
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query;

import com.szlazakm.safechat.contacts.data.Entities.MessageEntity;

import java.util.List;

@Dao
interface MessageDao {

    @Query("SELECT * FROM message_entity ORDER BY timestamp DESC")
    fun getMessages(): List<MessageEntity>

    @Insert
    fun insertMessage(contact: MessageEntity)

    @Delete
    fun deleteMessage(contact: MessageEntity)

    @Query("SELECT * FROM message_entity WHERE id = :id")
    fun getMessage(id: Long): MessageEntity?

    @Query("DELETE FROM message_entity")
    fun deleteAllMessages()

}
