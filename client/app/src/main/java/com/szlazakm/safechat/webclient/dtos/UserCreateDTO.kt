package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class UserCreateDTO(
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("phoneNumber")
    val phoneNumber: String,
    @JsonProperty("identityKey")
    val identityKey: String,
    @JsonProperty("pin")
    val pin: String
)