package com.szlazakm.safechat.client.presentation.states

import com.szlazakm.safechat.webclient.dtos.UserDTO

data class AddContactState (
    val phoneNumber: String = "",
    val userDTO: UserDTO?
)