package com.szlazakm.safechat.utils.auth.ecc

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class KeyConverter {

    companion object {

        fun toPrivateKey(key: ByteArray): PrivateKey {
            val keySpec = PKCS8EncodedKeySpec(key)
            val keyFactory = KeyFactory.getInstance("EC", "BC")
            return keyFactory.generatePrivate(keySpec)
        }

        fun toPublicKey(key: ByteArray): PublicKey {
            val keySpec = X509EncodedKeySpec(key)
            val keyFactory = KeyFactory.getInstance("EC", "BC")
            return keyFactory.generatePublic(keySpec)
        }
    }

}