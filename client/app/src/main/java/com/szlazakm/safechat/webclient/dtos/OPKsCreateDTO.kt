package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OPKsCreateDTO (
    @JsonProperty("phoneNumber")
    val phoneNumber: String,
    @JsonProperty("opkCreateDTOs")
    val opkCreateDTOs: List<OPKCreateDTO>,

    val nonceTimestamp: Long,
    val nonce: ByteArray,
    val authMessageSignature: ByteArray
)
