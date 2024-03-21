package com.szlazakm.safechat.contacts.presentation

enum class ScreenRoutes(val route: String) {
    SignIn("sing_in"),
    VerifyPhoneNumber("verify_phone_number"),
    SignInUserDetails("user_details"),
    SignInPin("pin"),
    VerifyPin("verify_pin"),
    ContactList("contact_list"),
    Chat("chat/{contactId}"),
    AddContact("add_contact")
}