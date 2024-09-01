package com.szlazakm.safechat.utils.auth.ecc

import android.util.Log
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

class EccKeyHelper {


    companion object {

        private var keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
            "ECDSA", "BC"
        )

        private var signatureGenerator: Signature = Signature.getInstance(
            "SHA256withECDSA", "BC"
        )

        fun generateKeyPair(): EccKeyPair {

//            val curve25519KeyPair = Curve25519.getInstance(BEST).generateKeyPair()
            val keyPair = keyPairGenerator.generateKeyPair()
            val privKeyBytes = keyPair.private.encoded
            val pubKeyBytes = keyPair.public.encoded
            return EccKeyPair(privKeyBytes, pubKeyBytes)
        }

        fun generateSignedKeyPair(privateIdentityKey: ByteArray): EccSignedKeyPair {

//            val keyId = SecureRandom().nextInt()
//            val curve25519KeyPair = Curve25519.getInstance(BEST).generateKeyPair()
//            val signature = Curve25519.getInstance(BEST)
//                .calculateSignature(privateIdentityKey, curve25519KeyPair.publicKey)
//
//            val timestamp: Long = System.currentTimeMillis()
//            return EccSignedKeyPair(
//                curve25519KeyPair.privateKey, curve25519KeyPair.publicKey, signature, keyId, timestamp
//            )

            // Decode the private key

            Log.i("EccKeyHelper", "Generating signed key pair")

            val keySpec = PKCS8EncodedKeySpec(privateIdentityKey)
            val keyFactory = KeyFactory.getInstance("EC", "BC")
            val privateKey = keyFactory.generatePrivate(keySpec)

            Log.i("EccKeyHelper", "Private signed key: $privateKey")

            val keyId = SecureRandom().nextInt()
            val eccKeyPair = generateKeyPair()

            signatureGenerator.initSign(privateKey)
            signatureGenerator.update(eccKeyPair.publicKey)

            val signatureBytes = signatureGenerator.sign()
            val timestamp: Long = System.currentTimeMillis()

            return EccSignedKeyPair(
                eccKeyPair.privateKey, eccKeyPair.publicKey, signatureBytes, keyId, timestamp
            )
        }

        fun generateOpks(start: Int, count: Int): List<EccOpk> {

            val opks = mutableListOf<EccOpk>()

            for(i in start until start + count) {

                val curve25519KeyPair = generateKeyPair()
                opks.add(EccOpk(
                    curve25519KeyPair.privateKey,
                    curve25519KeyPair.publicKey,
                    i
                ))
            }

            return opks
        }

        fun generateSenderKeyPair(): EccKeyPair {

            val curve25519KeyPair = generateKeyPair()
            return EccKeyPair(curve25519KeyPair.privateKey, curve25519KeyPair.publicKey)
        }

        fun verifySignature(signingKey: ByteArray, message: ByteArray, signature: ByteArray) : Boolean {

            val keySpec = PKCS8EncodedKeySpec(signingKey)
            val keyFactory = KeyFactory.getInstance("EC", "BC")
            val publicKey = keyFactory.generatePublic(keySpec)

            signatureGenerator.initVerify(publicKey)
            signatureGenerator.update(message)
            return signatureGenerator.verify(signature)
        }
    }

}