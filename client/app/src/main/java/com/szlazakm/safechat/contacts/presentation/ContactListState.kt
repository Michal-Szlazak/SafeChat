package com.szlazakm.safechat.contacts.presentation

import com.szlazakm.safechat.contacts.domain.Contact

data class ContactListState(
    val contacts: List<Contact> = emptyList(),
    val recentlyActiveContacts: List<Contact> = emptyList()
)
