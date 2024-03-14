package com.szlazakm.safechat.contacts.presentation

import com.szlazakm.safechat.contacts.domain.Contact

data class ContactListState(
    val contacts: List<Contact> = emptyList(),
    val recentlyActiveContacts: List<Contact> = emptyList(),
    val selectedContact: Contact? = null,
    val isSelectedChatSheetOpen: Boolean = false,
    val isAddChatSheetOpen: Boolean = false
)
