package com.szlazakm.safechat.contacts.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.szlazakm.safechat.contacts.domain.Contact
import java.util.UUID

@Entity(tableName = "contact_entity")
data class ContactEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: Long,
    val photo: String?
)

fun ContactEntity.toContact(): Contact {
    return Contact(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        photo = null
    )
}