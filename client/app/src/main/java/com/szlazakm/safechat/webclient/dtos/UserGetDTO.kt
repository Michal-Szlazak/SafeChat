package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class UserGetDTO(
    @JsonProperty("phoneNumber")
    val phoneNumber: String
)
