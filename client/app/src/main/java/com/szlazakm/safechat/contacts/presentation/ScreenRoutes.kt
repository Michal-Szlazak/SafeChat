package com.szlazakm.safechat.contacts.presentation

enum class ScreenRoutes(val route: String) {
    ContactList("contact_list"),
    Chat("chat/{contactId}"),
    AddContact("add_contact")
}