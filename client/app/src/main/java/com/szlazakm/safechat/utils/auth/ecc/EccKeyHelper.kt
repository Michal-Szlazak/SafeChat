package com.szlazakm.safechat.utils.auth.ecc

import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.whispersystems.curve25519.Curve25519
import org.whispersystems.curve25519.Curve25519.BEST
import java.security.SecureRandom
import java.security.Security

class EccKeyHelper() {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    companion object {

        val curve = Curve25519.getInstance(BEST)

        fun generateKeyPair(): EccKeyPair {
            val keyPair = curve.generateKeyPair()
            val privKeyBytes = keyPair.privateKey
            val pubKeyBytes = keyPair.publicKey
            return EccKeyPair(privKeyBytes, pubKeyBytes)
        }

        fun generateSignedKeyPair(privateIdentityKey: ByteArray): EccSignedKeyPair {

            val keyId = SecureRandom().nextInt()
            val eccKeyPair = generateKeyPair()

            val signatureBytes = curve.calculateSignature(
                privateIdentityKey, eccKeyPair.publicKey
            )
            val timestamp: Long = System.currentTimeMillis()

            return EccSignedKeyPair(
                eccKeyPair.privateKey, eccKeyPair.publicKey, signatureBytes, keyId, timestamp
            )
        }

        fun generateOpks(start: Int, count: Int): List<EccOpk> {

            val opks = mutableListOf<EccOpk>()

            for(i in start until start + count) {

                val curve25519KeyPair = curve.generateKeyPair()
                opks.add(EccOpk(
                    curve25519KeyPair.privateKey,
                    curve25519KeyPair.publicKey,
                    i
                ))
            }

            return opks
        }

        fun verifySignature(keyBundleDTO: KeyBundleDTO) : Boolean{

            val identityKeyBytes = Decoder.decode(keyBundleDTO.identityKey)
            val signedKeyBytes = Decoder.decode(keyBundleDTO.signedPreKey)
            val signatureBytes = Decoder.decode(keyBundleDTO.signature)

            return verifySignature(
                identityKeyBytes,
                signedKeyBytes,
                signatureBytes
            )
        }

        private fun verifySignature(signingKey: ByteArray, message: ByteArray, signature: ByteArray) : Boolean {

            return curve.verifySignature(
                signingKey, message, signature
            )
        }
    }
}