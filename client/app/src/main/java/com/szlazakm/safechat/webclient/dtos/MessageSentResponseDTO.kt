package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageSentResponseDTO (
    @JsonProperty("timestamp")
    val timestamp: String
)