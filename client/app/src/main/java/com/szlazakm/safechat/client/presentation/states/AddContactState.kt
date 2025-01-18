package com.szlazakm.safechat.client.presentation.states

import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.webclient.dtos.UserDTO

data class AddContactState (
    val phoneNumber: String = "",
    val userDTO: UserDTO?,
    val localUser: UserEntity?
)