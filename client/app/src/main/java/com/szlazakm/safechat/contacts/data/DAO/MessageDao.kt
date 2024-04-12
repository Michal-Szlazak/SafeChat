package com.szlazakm.safechat.contacts.data.DAO;

import androidx.room.Dao;
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query;

import com.szlazakm.safechat.contacts.data.Entities.MessageEntity;

import java.util.UUID

@Dao
interface MessageDao {

    @Query("SELECT * FROM message_entity" +
            " WHERE senderPhoneNumber=:senderPhoneNumber" +
            " AND receiverPhoneNumber=:receiverPhoneNumber" +
            " ORDER BY timestamp DESC")
    fun getMessages(senderPhoneNumber: String, receiverPhoneNumber: String): List<MessageEntity>

    @Insert
    fun insertMessage(contact: MessageEntity)

    @Delete
    fun deleteMessage(contact: MessageEntity)

    @Query("SELECT * FROM message_entity WHERE id = :id")
    fun getMessage(id: Long): MessageEntity?

    @Query("DELETE FROM message_entity")
    fun deleteAllMessages()

}
