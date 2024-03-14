package com.szlazakm.safechat.contacts.presentation.States

import com.szlazakm.safechat.contacts.domain.Contact

data class ContactListState(
    val contacts: List<Contact> = emptyList(),
    val recentlyActiveContacts: List<Contact> = emptyList(),
    val selectedContact: Contact? = null
)
