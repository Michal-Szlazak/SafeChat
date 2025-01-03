package com.szlazakm.safechat.webclient.dtos

data class GetOpksDTO(
    val phoneNumber: String,
    val nonceTimestamp: Long,
    val nonce: ByteArray,
    val authMessageSignature: ByteArray
) { }