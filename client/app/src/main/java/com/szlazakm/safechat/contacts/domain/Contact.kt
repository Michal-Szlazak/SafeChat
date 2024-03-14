package com.szlazakm.safechat.contacts.domain

import androidx.compose.ui.graphics.ImageBitmap

data class Contact(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val photo: ImageBitmap?
)