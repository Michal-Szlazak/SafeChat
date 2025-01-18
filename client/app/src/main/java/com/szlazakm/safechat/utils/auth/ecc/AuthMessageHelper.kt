package com.szlazakm.safechat.utils.auth.ecc

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.whispersystems.curve25519.Curve25519
import org.whispersystems.curve25519.Curve25519.BEST
import java.security.SecureRandom
import java.security.Security
import java.security.Signature

class AuthMessageHelper {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    companion object {

        val curve = Curve25519.getInstance(BEST)

        fun generateSignature(privateIdentityKey: ByteArray, message: ByteArray): ByteArray {

            return curve.calculateSignature(privateIdentityKey, message)
        }

        fun generateNonce(): ByteArray {
            val nonce = ByteArray(32)
            SecureRandom().nextBytes(nonce)
            return nonce
        }

    }

}