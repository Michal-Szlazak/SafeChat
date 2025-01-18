package com.szlazakm.chatserver.utils;

import org.springframework.stereotype.Service;
import org.whispersystems.curve25519.Curve25519;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class SignatureVerifier {
    private final Curve25519 curve25519 = Curve25519.getInstance(Curve25519.BEST);

    public boolean verifySignature(String signingKey, String signedKey, String signature) {

        byte[] signingKeyBytes = decode(signingKey);
        byte[] signedKeyBytes = decode(signedKey);
        byte[] signatureBytes = decode(signature);

//        this.signature.initVerify(publicKey);
//        this.signature.update(signedKeyBytes);
//        return this.signature.verify(signatureBytes);

        return curve25519.verifySignature(
                signingKeyBytes,
                signedKeyBytes,
                signatureBytes
        );
    }

    public boolean verifySignature(String signingKey, byte[] signedKey, byte[] signature) {

        byte[] signingKeyBytes = decode(signingKey);

//        this.signature.initVerify(publicKey);
//        this.signature.update(signedKey);
//        return this.signature.verify(signature);

        return curve25519.verifySignature(
                signingKeyBytes,
                signedKey,
                signature
        );
    }

    private byte[] decode(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}
