package com.szlazakm.safechat.contacts.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.szlazakm.safechat.contacts.domain.Contact

@Entity(tableName = "contact_entity")
data class ContactEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val createdAt: Long,
    val photo: String?
)

fun ContactEntity.toContact(): Contact {
    return Contact(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        photo = null
    )
}