package com.szlazakm.safechat.utils.auth.ecc

import android.util.Log
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class EccKeyHelper() {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }


    companion object {

        private var keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
            "ECDSA", "BC"
        )

        private var signatureGenerator: Signature = Signature.getInstance(
            "SHA256withECDSA", "BC"
        )

        fun generateKeyPair(): EccKeyPair {
            val keyPair = keyPairGenerator.generateKeyPair()
            val privKeyBytes = keyPair.private.encoded
            val pubKeyBytes = keyPair.public.encoded
            return EccKeyPair(privKeyBytes, pubKeyBytes)
        }

        fun generateSignedKeyPair(privateIdentityKey: ByteArray): EccSignedKeyPair {

            Log.i("EccKeyHelper", "Generating signed key pair")

            val privateKey = KeyConverter.toPrivateKey(privateIdentityKey)

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

            val publicKey = KeyConverter.toPublicKey(signingKey)

            signatureGenerator.initVerify(publicKey)
            signatureGenerator.update(message)
            return signatureGenerator.verify(signature)
        }
    }

}