package com.szlazakm.chatserver.auth;

import com.szlazakm.chatserver.exceptionHandling.exceptions.SignatureVerifierException;
import com.szlazakm.chatserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class SignatureVerifier {

    public boolean verify(String message, String signedMessage, String publicKey) {

        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey ecPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            Signature ecdsaSignature = Signature.getInstance("SHA256withECDSA");

            ecdsaSignature.initVerify(ecPublicKey);

            ecdsaSignature.update(message.getBytes());

            return ecdsaSignature.verify(Base64.getDecoder().decode(signedMessage));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException ex) {
            throw new SignatureVerifierException("Failed to verify the signature. Message: " + ex.getMessage());
        }

    }

}
