package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.entities.MessageEntity

@Dao
interface MessageDao {

    @Query("SELECT * FROM message_entity" +
            " WHERE (senderPhoneNumber=:senderPhoneNumber" +
            " AND receiverPhoneNumber=:receiverPhoneNumber)" +
            " OR (senderPhoneNumber=:receiverPhoneNumber" +
            " AND receiverPhoneNumber=:senderPhoneNumber)" +
            " ORDER BY timestamp DESC")
    fun getMessages(senderPhoneNumber: String, receiverPhoneNumber: String): List<MessageEntity>


    @Insert
    fun insertMessage(contact: MessageEntity)

    @Delete
    fun deleteMessage(contact: MessageEntity)

    @Query("SELECT * FROM message_entity WHERE id = :id")
    fun getMessage(id: Long): MessageEntity?

    @Query("SELECT * FROM message_entity")
    fun getAllMessages(): List<MessageEntity>

    @Query("DELETE FROM message_entity")
    fun deleteAllMessages()

}
