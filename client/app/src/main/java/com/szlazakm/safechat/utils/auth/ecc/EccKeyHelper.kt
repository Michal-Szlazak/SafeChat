package com.szlazakm.safechat.utils.auth.ecc

import org.whispersystems.curve25519.Curve25519
import org.whispersystems.curve25519.Curve25519.BEST
import java.security.SecureRandom

class EccKeyHelper {

    companion object {

        fun generateKeyPair(): EccKeyPair {

            val curve25519KeyPair = Curve25519.getInstance(BEST).generateKeyPair()
            return EccKeyPair(curve25519KeyPair.privateKey, curve25519KeyPair.publicKey)
        }

        fun generateSignedKeyPair(privateIdentityKey: ByteArray): EccSignedKeyPair {

            val keyId = SecureRandom().nextInt()
            val curve25519KeyPair = Curve25519.getInstance(BEST).generateKeyPair()
            val signature = Curve25519.getInstance(BEST)
                .calculateSignature(privateIdentityKey, curve25519KeyPair.publicKey)

            val timestamp: Long = System.currentTimeMillis()
            return EccSignedKeyPair(
                curve25519KeyPair.privateKey, curve25519KeyPair.publicKey, signature, keyId, timestamp
            )
        }

        fun generateOpks(start: Int, count: Int): List<EccOpk> {

            val opks = mutableListOf<EccOpk>()

            for(i in start until start + count) {

                val curve25519KeyPair = Curve25519.getInstance(BEST).generateKeyPair()
                opks.add(EccOpk(curve25519KeyPair.privateKey, curve25519KeyPair.publicKey, i))
            }

            return opks
        }

        fun generateSenderKeyPair(): EccKeyPair {

            val curve25519KeyPair = Curve25519.getInstance(BEST).generateKeyPair()
            return EccKeyPair(curve25519KeyPair.privateKey, curve25519KeyPair.publicKey)
        }
    }

}