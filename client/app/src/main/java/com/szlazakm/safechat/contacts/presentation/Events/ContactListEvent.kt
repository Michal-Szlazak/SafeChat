package com.szlazakm.safechat.contacts.presentation

import com.szlazakm.safechat.contacts.domain.Contact

sealed interface ContactListEvent {
    data class OnConversationClick(val contact: Contact): ContactListEvent
    object DismissConversation: ContactListEvent
    object OnNewConversationClick: ContactListEvent
    object DismissNewConversation: ContactListEvent
    data class OnFirstNameChanged(val value: String): ContactListEvent
    data class OnLastNameChanged(val value: String): ContactListEvent
}