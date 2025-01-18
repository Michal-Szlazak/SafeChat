package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.szlazakm.safechat.client.domain.Contact

@Entity(tableName = "contact_entity")
data class ContactEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val securityCode: String
)

fun ContactEntity.toContact(): Contact {
    return Contact(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        photo = null,
        securityCode = this.securityCode
    )
}