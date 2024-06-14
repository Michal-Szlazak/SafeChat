package com.szlazakm.safechat.webclient.dtos

data class MessageDTO(
    val from: String,
    val to: String,
    val text: String
)
