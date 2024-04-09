package com.szlazakm.safechat.contacts.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.szlazakm.safechat.contacts.data.Entities.ContactEntity
import java.util.Date
import java.util.UUID

data class Contact(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val photo: ImageBitmap?
)

fun Contact.toContactEntity(): ContactEntity {
    return ContactEntity(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        photo = null,
        createdAt = 1
    )
}