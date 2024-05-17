package com.szlazakm.safechat.utils.auth

import org.whispersystems.curve25519.Curve25519

class DiffieHellman {

    companion object {
        fun createSharedSecret(privateKeyBytes: ByteArray, publicKeyBytes: ByteArray): ByteArray {

            return Curve25519.getInstance(Curve25519.BEST).calculateAgreement(publicKeyBytes, privateKeyBytes)
        }
    }

}