package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class MessageAcknowledgementDTO (
    @JsonProperty("messageId")
    val messageId: UUID
)