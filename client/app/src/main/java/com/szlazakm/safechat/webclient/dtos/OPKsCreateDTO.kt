package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OPKsCreateDTO (
    @JsonProperty("phoneNumber")
    val phoneNumber: String,
    @JsonProperty("opkCreateDTOs")
    val opkCreateDTOs: List<OPKCreateDTO>
)
