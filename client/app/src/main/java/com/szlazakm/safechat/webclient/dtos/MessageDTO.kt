package com.szlazakm.safechat.webclient.dtos

import java.time.Instant

data class MessageDTO(
    val from: String,
    val to: String,
    val text: String,

    val phoneNumber: String,
    val nonceTimestamp: Long,
    val nonce: ByteArray,
    val authMessageSignature: ByteArray
)
