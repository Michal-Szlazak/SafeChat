package com.szlazakm.safechat.client.presentation

enum class ScreenRoutes(val route: String) {
    LoadingPage("loading_page"),
    SignIn("sing_in"),
    VerifyPhoneNumber("verify_phone_number"),
    SignInUserDetails("user_details"),
    SignInPin("pin"),
    VerifyPin("verify_pin"),
    ContactList("contact_list"),
    Chat("chat/{phoneNumber}"),
    AddContact("add_contact")
}