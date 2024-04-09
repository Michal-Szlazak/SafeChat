package com.szlazakm.safechat.webclient.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class UserDTO(

    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("phoneNumber")
    val phoneNumber: String
)
