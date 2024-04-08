package com.szlazakm.safechat.utils.auth

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey

fun generateKeyPair(): PublicKey {

    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(2048)
    return keyPairGenerator.generateKeyPair().public
}