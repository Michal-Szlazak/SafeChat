package com.szlazakm.safechat.webclient.dtos

data class OutputMessageDTO(
    val from: String,
    val to: String,
    val text: String,
    val date: String
)
