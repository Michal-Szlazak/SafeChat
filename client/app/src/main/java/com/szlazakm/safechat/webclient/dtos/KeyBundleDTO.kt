package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class KeyBundleDTO (
    @JsonProperty("identityKey")
    val identityKey: String,
    @JsonProperty("signedPreKeyId")
    val signedPreKeyId: Int,
    @JsonProperty("signedPreKey")
    val signedPreKey: String,
    @JsonProperty("signature")
    val signature: String,
    @JsonProperty("onetimePreKeyId")
    val onetimePreKeyId: Int?,
    @JsonProperty("onetimePreKey")
    val onetimePreKey: String?
)
