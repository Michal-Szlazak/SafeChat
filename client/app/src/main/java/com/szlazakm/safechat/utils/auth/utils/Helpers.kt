package com.szlazakm.safechat.utils.auth.utils

open class Helpers {

    open fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}