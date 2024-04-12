package com.szlazakm.safechat.contacts.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.contacts.data.Entities.ContactEntity
import java.util.UUID

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_entity ORDER BY firstName ASC")
    fun getContacts(): List<ContactEntity>

    @Query("SELECT * FROM contact_entity ORDER BY createdAt DESC LIMIT :amount")
    fun getRecentContacts(amount: Int): List<ContactEntity>

    @Insert
    fun insertContact(contact: ContactEntity)

    @Delete
    fun deleteContact(contact: ContactEntity)

    @Query("SELECT * FROM contact_entity WHERE phoneNumber = :phoneNumber")
    fun getContact(phoneNumber: String): ContactEntity?

    @Query("DELETE FROM contact_entity")
    fun deleteAllContacts()
}