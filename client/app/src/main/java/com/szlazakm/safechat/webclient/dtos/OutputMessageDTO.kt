package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class OutputMessageDTO(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("from")
    val from: String,
    @JsonProperty("to")
    val to: String,
    @JsonProperty("text")
    val text: String,
    @JsonProperty("date")
    val date: String
)
