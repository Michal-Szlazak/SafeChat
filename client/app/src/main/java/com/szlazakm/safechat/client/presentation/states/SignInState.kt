package com.szlazakm.safechat.client.presentation.states

data class SignInState(
    val isUserCreated: Boolean = false,
    val phoneNumber: String = "",
    val extension: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val pin: String = "",
    val phoneVerifyError: String = "",
    val phoneVerificationResult : Boolean? = false,
    val isLoading : Boolean = true
)