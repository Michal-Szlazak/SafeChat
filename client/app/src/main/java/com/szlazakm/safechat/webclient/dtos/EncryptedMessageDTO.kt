package com.szlazakm.safechat.webclient.dtos

import java.time.Instant

data class EncryptedMessageDTO (
    val initial: Boolean,
    val from: String,
    val to: String,
    val cipher: String,
    val aliceIdentityPublicKey: String?,
    val aliceEphemeralPublicKey: String?,
    val bobOpkId: Int?,
    val bobSpkId: Int?,
    val ephemeralRatchetKey: String,
    val messageIndex: Int,
    val lastMessageBatchSize: Int,

    val phoneNumber: String,
    val nonceTimestamp: Long,
    val nonce: ByteArray,
    val authMessageSignature: ByteArray
)
