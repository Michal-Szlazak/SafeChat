package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OPKCreateDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("preKey")
    val preKey: String
)
