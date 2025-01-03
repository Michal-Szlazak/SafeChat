package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.util.UUID

data class MessageAcknowledgementDTO (
    @JsonProperty("messageId")
    val messageId: UUID,

    val phoneNumber: String,
    val nonceTimestamp: Long,
    val nonce: ByteArray,
    val authMessageSignature: ByteArray
)