package com.szlazakm.safechat.client.presentation.Events

import com.szlazakm.safechat.client.domain.Contact

sealed interface ContactListEvent {
    data class OnConversationClick(val contact: Contact): ContactListEvent
    object OnNewConversationClick: ContactListEvent
}