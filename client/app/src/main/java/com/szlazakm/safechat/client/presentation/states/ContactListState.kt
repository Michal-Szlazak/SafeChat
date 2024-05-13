package com.szlazakm.safechat.client.presentation.States

import androidx.lifecycle.LiveData
import com.szlazakm.safechat.client.data.entities.ContactEntity
import com.szlazakm.safechat.client.domain.Contact

data class ContactListState(
    val contacts: List<Contact> = emptyList(),
    val recentlyActiveContacts: List<Contact> = emptyList(),
    val selectedContact: Contact? = null
)
