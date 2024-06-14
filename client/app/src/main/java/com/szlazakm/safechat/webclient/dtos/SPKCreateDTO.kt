package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class SPKCreateDTO (
    @JsonProperty("phoneNumber")
    val phoneNumber: String,
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("signedPreKey")
    val signedPreKey: String,
    @JsonProperty("signature")
    val signature: String,
    @JsonProperty("timestamp")
    val timestamp: Long
)
