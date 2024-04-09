package com.szlazakm.safechat.contacts.data.Repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.contacts.data.Entities.ContactEntity
import com.szlazakm.safechat.contacts.data.Entities.toContact
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.toContactEntity
import java.util.UUID
import javax.inject.Inject

class ContactRepository @Inject constructor(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration()
        .build()

    // Function to insert hardcoded contacts into the database
    fun insertHardcodedContacts() {
        // Create an instance of the DAO
        val contactDao = database.contactDao()
        contactDao.deleteAllContacts()

        // Insert hardcoded contacts
        val contacts = listOf(
            ContactEntity(
                id = UUID.fromString("389ea0de-0758-4636-9ef0-6a38d1a12162"),
                firstName = "John",
                lastName = "Doe",
                phoneNumber = "1234567890",
                email = "john@example.com",
                createdAt = System.currentTimeMillis(),
                photo = "/path/to/image.jpg"
            ),
            ContactEntity(
                id = UUID.fromString("389ea0de-0758-4636-9ef0-6a38d1a12163"),
                firstName = "Jane",
                lastName = "Doe",
                phoneNumber = "0987654321",
                email = "jane@example.com",
                createdAt = System.currentTimeMillis(),
                photo = "/path/to/another/image.jpg"
            )
            // Add more contacts as needed
        )

        contacts.forEach { contact ->
            contactDao.insertContact(contact)
        }
    }

    fun getContacts(): List<Contact> {
        return database.contactDao().getContacts().map { c -> c.toContact() }
    }

    fun getRecentContacts(): List<Contact> {
        return database.contactDao().getRecentContacts(10).map {
            c -> c.toContact() }
    }

    fun deleteAllContacts() {
        database.contactDao().deleteAllContacts()
    }

    fun getContact(userId: UUID) : Contact?{
        return database.contactDao().getContact(userId)?.toContact()
    }

    fun createContact(contact: Contact) {
        database.contactDao().insertContact(contact.toContactEntity())
    }
}