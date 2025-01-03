package com.szlazakm.safechat.utils.auth.utils

import java.util.Base64

class Decoder {

    companion object {
        fun decode(encodedString: String): ByteArray {
            return Base64.getDecoder().decode(encodedString)
        }
    }
}