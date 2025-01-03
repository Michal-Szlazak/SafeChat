package com.szlazakm.safechat.excpetions

class LocalUserNotFoundException : Exception() {
    override val message: String
        get() = "Local user not found"
}