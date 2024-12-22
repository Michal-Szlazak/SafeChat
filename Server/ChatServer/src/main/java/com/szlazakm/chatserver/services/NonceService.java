package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.*;
import com.szlazakm.chatserver.repositories.UserRepository;
import com.szlazakm.chatserver.utils.SignatureVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NonceService {

    private final SignatureVerifier signatureVerifier;
    private final UserRepository userRepository;
    private final Map<String, Instant> nonceStore = new ConcurrentHashMap<>();
    private static final long EXPIRATION_DURATION_SECONDS = 300; // 5 minutes

    public void handleAuthMessage(String phoneNumber, byte[] nonce, Long timestamp, byte[] signature) {

        boolean isSignatureValid;

        byte[] timestampBytes = Base64.getDecoder().decode(timestamp.toString());
        byte[] combined = new byte[nonce.length + timestampBytes.length];

        System.arraycopy(nonce, 0,combined, 0, nonce.length);
        System.arraycopy(timestampBytes, 0,combined, nonce.length, timestampBytes.length);

        try {
            isSignatureValid = verifySignature(phoneNumber, combined, signature);
        } catch (Exception e) {
            throw new SignatureVerifierException(e.getMessage());
        }

        if(!isSignatureValid) {
            throw new SignatureVerifierException("Signature verification returned false.");
        }

        Instant instant = Instant.ofEpochSecond(timestamp);
        if(isExpired(instant)) {
            throw new ExpiredNonceException();
        }

        if(isPresent(nonce)) {
            throw new ReusedNonceException();
        }

        addNonce(nonce, instant);
    }

    private void addNonce(byte[] nonce, Instant instant) {

        if(instant.isAfter(Instant.now())) {
            throw new IllegalNonceException("Nonce cannot have future date.");
        }

        String nonceString = Base64.getEncoder().encodeToString(nonce);
        nonceStore.put(nonceString, instant);
    }

    private boolean isPresent(byte[] nonce) {
        String nonceString = Base64.getEncoder().encodeToString(nonce);
        return nonceStore.containsKey(nonceString);
    }

    private boolean isExpired(Instant timestamp) {

        Instant future = timestamp.plusSeconds(EXPIRATION_DURATION_SECONDS);
        Instant now = Instant.now();

        return now.isAfter(future);
    }

    private boolean verifySignature(String phoneNumber, byte[] message, byte[] signature)
            throws InvalidKeySpecException, SignatureException, InvalidKeyException
    {

        Optional<User> optUser = userRepository.findByPhoneNumber(phoneNumber);
        User user = optUser.orElseThrow(UserNotFoundException::new);

        return signatureVerifier.verifySignature(
                user.getIdentityKey(),
                message,
                signature
        );
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES) // Run every 1 minute
    public void cleanExpiredNonces() {
        nonceStore.entrySet().removeIf(entry -> {
            Instant timestamp = entry.getValue();
            return timestamp.isAfter(timestamp.plusSeconds(60L * 10L));
        });
    }
}
