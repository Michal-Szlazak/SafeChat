package com.szlazakm.safechat.contacts.presentation

sealed interface ContactListEvent {
    object OnConversationClick: ContactListEvent
    object DismissConversation: ContactListEvent
    object OnNewConversationClick: ContactListEvent
}