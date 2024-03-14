package com.szlazakm.safechat.contacts.presentation.Events

import com.szlazakm.safechat.contacts.domain.Contact

sealed interface ContactListEvent {
    data class OnConversationClick(val contact: Contact): ContactListEvent
    object OnNewConversationClick: ContactListEvent
}