package com.szlazakm.safechat.utils.auth.utils

import java.util.Base64

class Encoder {

    companion object {
        fun encode(bytes: ByteArray): String {
            return Base64.getEncoder().encodeToString(bytes)
        }
    }
}