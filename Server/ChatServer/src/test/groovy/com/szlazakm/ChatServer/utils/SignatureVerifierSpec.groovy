package com.szlazakm.ChatServer.utils

import com.szlazakm.ChatServer.helpers.TestSignatureProvider
import com.szlazakm.chatserver.utils.SignatureVerifier
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification
import java.security.Security

class SignatureVerifierSpec extends Specification{

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    def signatureVerifier = new SignatureVerifier()
    def encoder = Base64.getEncoder()

    def "should return true if the signature is correct"() {

        given:
        def keyPair = TestSignatureProvider.createKeyPair()
        def keyToSign = TestSignatureProvider.createKeyPair()
        def signature = TestSignatureProvider.createSignature(keyPair.private, keyToSign.public)

        when:
        def result = signatureVerifier.verifySignature(
                encoder.encodeToString(keyPair.public.encoded),
                encoder.encodeToString(keyToSign.public.encoded),
                signature
        )

        then:
        result
    }

    def "should return false if the signature is incorrect"() {

        given:
        def keyPair = TestSignatureProvider.createKeyPair()
        def keyToSign = TestSignatureProvider.createKeyPair()
        def otherKeyToSign = TestSignatureProvider.createKeyPair()
        def incorrectSignature = TestSignatureProvider.createSignature(keyPair.private, otherKeyToSign.public)

        when:
        def result = signatureVerifier.verifySignature(
                encoder.encodeToString(keyPair.public.encoded),
                encoder.encodeToString(keyToSign.public.encoded),
                incorrectSignature
        )

        then:
        !result
    }

    def "should return false if try to replace signed key and signature is incorrect"() {

        given:
        def keyPair = TestSignatureProvider.createKeyPair()

        def falseKeyPair = TestSignatureProvider.createKeyPair()
        def keyToSign = TestSignatureProvider.createKeyPair()
        def signature = TestSignatureProvider.createSignature(falseKeyPair.private, keyToSign.public)

        when:
        def result = signatureVerifier.verifySignature(
                encoder.encodeToString(keyPair.public.encoded),
                encoder.encodeToString(falseKeyPair.public.encoded),
                signature
        )

        then:
        !result
    }
}
