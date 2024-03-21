package com.szlazakm.safechat.contacts.presentation.States

data class SignInState(
    val isUserCreated: Boolean = false,
    val phoneNumber: String = "",
    val extension: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val pin: String = "",
    val phoneVerifyError: String = ""
)