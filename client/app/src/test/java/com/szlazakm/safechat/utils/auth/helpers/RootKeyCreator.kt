package com.szlazakm.safechat.utils.auth.helpers

import com.szlazakm.safechat.utils.auth.ecc.RootKey

class RootKeyCreator {

    companion object {

        fun createRootKey(): RootKey {
            return RootKey(
                key = "symmetric key".toByteArray()
            )
        }
    }
}