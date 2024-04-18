package com.szlazakm.safechat.client.data.Repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.Entities.ContactEntity
import com.szlazakm.safechat.client.data.Entities.toContact
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.toContactEntity
import javax.inject.Inject

class ContactRepository @Inject constructor(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration()
        .build()

    fun clearContacts() {
        val contactDao = database.contactDao()
        contactDao.deleteAllContacts()
    }

    fun getContacts(): List<Contact> {
        return database.contactDao().getContacts().map { c -> c.toContact() }
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