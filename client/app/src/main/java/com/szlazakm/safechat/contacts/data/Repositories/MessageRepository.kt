package com.szlazakm.safechat.contacts.data.Repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.contacts.data.Entities.ContactEntity
import com.szlazakm.safechat.contacts.data.Entities.MessageEntity
import com.szlazakm.safechat.contacts.data.Entities.toContact
import com.szlazakm.safechat.contacts.data.Entities.toTextMessage
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.Message
import java.util.Date
import javax.inject.Inject

class MessageRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    // Function to insert hardcoded contacts into the database
    fun insertHardcodedMessages() {
        // Create an instance of the DAO
        val messageDao = database.messageDao()
        messageDao.deleteAllMessages()

        val user1 = ContactEntity(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john@example.com",
            createdAt = System.currentTimeMillis(),
            photo = "/path/to/image.jpg"
        )
        val user2 = ContactEntity(
            id = 2,
            firstName = "Jane",
            lastName = "Doe",
            phoneNumber = "0987654321",
            email = "jane@example.com",
            createdAt = System.currentTimeMillis(),
            photo = "/path/to/another/image.jpg"
        )

        // Insert hardcoded contacts
        val messages = listOf(
            MessageEntity(
                content = "hello to you",
                senderId = user1.id,
                receiverId = user2.id,
                timestamp = Date()
            ),
            MessageEntity(
                content = "hello to you too",
                senderId = user2.id,
                receiverId = user1.id,
                timestamp = Date()
            )
            // Add more contacts as needed
        )

        // Insert each contact into the database
        messages.forEach { message ->
            messageDao.insertMessage(message)
        }
    }

    fun getMessages(): List<Message.TextMessage> {
        return database.messageDao().getMessages().map { messageEntity -> messageEntity.toTextMessage() }
    }

    fun getRecentContacts(): List<Contact> {
        return database.contactDao().getRecentContacts(10).map {
                c -> c.toContact() }
    }

    fun deleteAllContacts() {
        database.contactDao().deleteAllContacts()
    }

}