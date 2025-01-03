package com.szlazakm.safechat.utils.auth.ecc

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.SecureRandom
import java.security.Security
import java.security.Signature

class AuthMessageHelper {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    companion object {

        private var signatureGenerator: Signature = Signature.getInstance(
            "SHA256withECDSA", "BC"
        )

        private var SecureRandom: SecureRandom = SecureRandom()

        fun generateSignature(privateIdentityKey: ByteArray, message: ByteArray): ByteArray {

            val privateKey = KeyConverter.toPrivateKey(privateIdentityKey)

            signatureGenerator.initSign(privateKey)
            signatureGenerator.update(message)

            return signatureGenerator.sign()
        }

        fun generateNonce(): ByteArray {
            val nonce = ByteArray(32)
            SecureRandom().nextBytes(nonce)
            return nonce
        }

    }

}