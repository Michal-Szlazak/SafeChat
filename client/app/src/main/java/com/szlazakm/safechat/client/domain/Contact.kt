package com.szlazakm.safechat.client.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.szlazakm.safechat.client.data.entities.ContactEntity

data class Contact(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val photo: ImageBitmap?,
    var securityCode: String
)

fun Contact.toContactEntity(): ContactEntity {
    return ContactEntity(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        photo = null,
        securityCode = this.securityCode
    )
}