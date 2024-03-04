package com.szlazakm.safechat.contacts.presentation.components

import androidx.lifecycle.ViewModel
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.presentation.ContactListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactListViewModel: ViewModel() {

    private val _state = MutableStateFlow(ContactListState(
        contacts = contacts
    ))
    val state = _state.asStateFlow()


}

private val contacts = (1..15).map{
    Contact(
        id = it.toLong(),
        firstName = "John$it",
        lastName = "Doe$it",
        email = "JohnDoe$it@gmail.com",
        phoneNumber = "123123123",
        photo = null
    )
}