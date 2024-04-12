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

    fun clearContacts() {
        val contactDao = database.contactDao()
        contactDao.deleteAllContacts()
    }

    fun insertHardcodedContacts() {
        // Create an instance of the DAO
        val contactDao = database.contactDao()
        contactDao.deleteAllContacts()

        // Insert hardcoded contacts
        val contacts = listOf(
            ContactEntity(
                firstName = "John",
                lastName = "Doe",
                phoneNumber = "1234567890",
                email = "john@example.com",
                createdAt = System.currentTimeMillis(),
                photo = "/path/to/image.jpg"
            ),
            ContactEntity(
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

    fun getContact(phoneNumber: String) : Contact?{
        return database.contactDao().getContact(phoneNumber)?.toContact()
    }

    fun createContact(contact: Contact) {
        database.contactDao().insertContact(contact.toContactEntity())
    }

    fun contactExists(phoneNumber: String) : Boolean {
        return database.contactDao().getContact(phoneNumber) != null
    }
}