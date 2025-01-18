package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.szlazakm.safechat.client.data.entities.ContactEntity

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_entity ORDER BY firstName ASC")
    fun getContacts(): List<ContactEntity>

    @Insert
    fun insertContact(contact: ContactEntity)

    @Delete
    fun deleteContact(contact: ContactEntity)

    @Query("SELECT * FROM contact_entity WHERE phoneNumber = :phoneNumber")
    fun getContact(phoneNumber: String): ContactEntity?

    @Query("DELETE FROM contact_entity")
    fun deleteAllContacts()

    @Update
    fun updateContact(contact: ContactEntity)
}