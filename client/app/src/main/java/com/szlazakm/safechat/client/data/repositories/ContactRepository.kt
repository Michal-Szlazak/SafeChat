package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.ContactEntity
import com.szlazakm.safechat.client.data.entities.toContact
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.toContactEntity
import javax.inject.Inject

class ContactRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration()
        .build()

    val allContacts: LiveData<List<ContactEntity>> = database.contactDao().getContacts()

//    fun getContacts(): LiveData<List<Contact>> {
//        // TODO: not implemented yet
//        return allContacts
//    }

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