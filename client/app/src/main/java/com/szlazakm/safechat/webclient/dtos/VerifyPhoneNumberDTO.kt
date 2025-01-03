package com.szlazakm.safechat.webclient.dtos

data class VerifyPhoneNumberDTO (
    val code : String,
    val phoneNumber: String
)
