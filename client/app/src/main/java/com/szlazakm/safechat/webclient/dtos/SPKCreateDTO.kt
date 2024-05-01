package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class SPKCreateDTO (
    @JsonProperty("phoneNumber")
    val phoneNumber: String,
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("signedPreKey")
    val signedPreKey: ByteArray,
    @JsonProperty("signature")
    val signature: ByteArray,
    @JsonProperty("timestamp")
    val timestamp: Long
)
