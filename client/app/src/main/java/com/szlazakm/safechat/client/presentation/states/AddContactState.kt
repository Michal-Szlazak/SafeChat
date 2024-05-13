package com.szlazakm.safechat.client.presentation.States

import com.szlazakm.safechat.webclient.dtos.UserDTO

data class AddContactState (
    val phoneNumber: String = "",
    val userDTO: UserDTO?
)