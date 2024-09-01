package com.szlazakm.safechat.utils.auth.utils

import com.szlazakm.safechat.utils.auth.ecc.KeyConverter
import javax.crypto.KeyAgreement

class DiffieHellman {


    fun createSharedSecret(privateKeyBytes: ByteArray, publicKeyBytes: ByteArray): ByteArray {

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        val privateKey = KeyConverter.toPrivateKey(privateKeyBytes)
        val publicKey = KeyConverter.toPublicKey(publicKeyBytes)

        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        return keyAgreement.generateSecret()
//        return Curve25519.getInstance(Curve25519.BEST).calculateAgreement(publicKeyBytes, privateKeyBytes)
    }

}