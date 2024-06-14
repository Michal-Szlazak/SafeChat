package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class OutputEncryptedMessageDTO(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("initial")
    val initial: Boolean,
    @JsonProperty("from")
    val from: String,
    @JsonProperty("to")
    val to: String,
    @JsonProperty("cipher")
    val cipher: String,
    @JsonProperty("aliceIdentityPublicKey")
    val aliceIdentityPublicKey: String?,
    @JsonProperty("aliceEphemeralPublicKey")
    val aliceEphemeralPublicKey: String?,
    @JsonProperty("bobOpkId")
    val bobOpkId: Int?,
    @JsonProperty("bobSpkId")
    val bobSpkId: Int?,
    @JsonProperty("date")
    val date: String
)