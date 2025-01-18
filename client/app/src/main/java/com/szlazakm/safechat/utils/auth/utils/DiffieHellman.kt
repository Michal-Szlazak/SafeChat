package com.szlazakm.safechat.utils.auth.utils

import org.whispersystems.curve25519.Curve25519
import javax.crypto.KeyAgreement

class DiffieHellman {

    companion object {

        val curve = Curve25519.getInstance(Curve25519.BEST)

        fun createSharedSecret(privateKeyBytes: ByteArray, publicKeyBytes: ByteArray): ByteArray {

            return curve.calculateAgreement(publicKeyBytes, privateKeyBytes)
        }
    }
}