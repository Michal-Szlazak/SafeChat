package com.szlazakm.ChatServer.helpers

import org.bouncycastle.jce.provider.BouncyCastleProvider

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.security.Signature

class TestSignatureProvider {

    static KeyPair createKeyPair() {

        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                "ECDSA", "BC"
        )

        keyPairGenerator.generateKeyPair()
    }

    static String createSignature(PrivateKey privateKey, PublicKey publicKey) {

        Security.addProvider(new BouncyCastleProvider());

        def signature = Signature.getInstance("SHA256withECDSA", "BC")
        signature.initSign(privateKey)
        signature.update(publicKey.encoded)
        return Base64.getEncoder().encodeToString(signature.sign())
    }

}