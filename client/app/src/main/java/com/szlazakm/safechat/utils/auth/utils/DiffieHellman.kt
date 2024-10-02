package com.szlazakm.safechat.utils.auth.utils

import com.szlazakm.safechat.utils.auth.ecc.KeyConverter
import javax.crypto.KeyAgreement

class DiffieHellman {

    companion object {
        fun createSharedSecret(privateKeyBytes: ByteArray, publicKeyBytes: ByteArray): ByteArray {
            return DiffieHellman().createSharedSecret(privateKeyBytes, publicKeyBytes)
        }
    }


    fun createSharedSecret(privateKeyBytes: ByteArray, publicKeyBytes: ByteArray): ByteArray {

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        val privateKey = KeyConverter.toPrivateKey(privateKeyBytes)
        val publicKey = KeyConverter.toPublicKey(publicKeyBytes)

        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        return keyAgreement.generateSecret()
    }

}