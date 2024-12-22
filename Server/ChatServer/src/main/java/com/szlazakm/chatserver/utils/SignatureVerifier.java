package com.szlazakm.chatserver.utils;

import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class SignatureVerifier {

    private final KeyFactory keyFactory;
    private final Signature signature;

    public SignatureVerifier() {
        try {
            keyFactory = KeyFactory.getInstance("EC", "BC");
            signature = Signature.getInstance("SHA256withECDSA", "BC");

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifySignature(String signingKey, String signedKey, String signature)
            throws InvalidKeySpecException, InvalidKeyException, SignatureException {

        byte[] signingKeyBytes = decode(signingKey);
        byte[] signedKeyBytes = decode(signedKey);
        byte[] signatureBytes = decode(signature);

        KeySpec keySpec = new X509EncodedKeySpec(signingKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        this.signature.initVerify(publicKey);
        this.signature.update(signedKeyBytes);
        return this.signature.verify(signatureBytes);
    }

    public boolean verifySignature(String signingKey, byte[] signedKey, byte[] signature)
            throws InvalidKeySpecException, InvalidKeyException, SignatureException {

        byte[] signingKeyBytes = decode(signingKey);

        KeySpec keySpec = new X509EncodedKeySpec(signingKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        this.signature.initVerify(publicKey);
        this.signature.update(signedKey);
        return this.signature.verify(signature);
    }

    private byte[] decode(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}
