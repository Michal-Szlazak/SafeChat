package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.AuthMessage;
import com.szlazakm.chatserver.exceptionHandling.exceptions.ExpiredNonceException;
import com.szlazakm.chatserver.exceptionHandling.exceptions.IllegalNonceException;
import com.szlazakm.chatserver.exceptionHandling.exceptions.ReusedNonceException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class NonceService {

    private final Map<String, Instant> nonceStore = new ConcurrentHashMap<>();
    private static final long EXPIRATION_DURATION_SECONDS = 300; // 5 minutes

    public <T extends AuthMessage> void handleAuthMessage(T t) {

        if(isExpired(t.getTimestamp())) {
            throw new ExpiredNonceException();
        }

        if(isPresent(t.getNonce())) {
            throw new ReusedNonceException();
        }

        addNonce(t.getNonce(), t.getTimestamp());
    }

    private void addNonce(String nonce, Instant instant) {

        if(instant.isAfter(Instant.now())) {
            throw new IllegalNonceException("Nonce cannot have future date.");
        }

        nonceStore.put(nonce, instant);
    }

    private boolean isPresent(String nonce) {
        return nonceStore.containsKey(nonce);
    }

    private boolean isExpired(Instant timestamp) {
        return Instant.now().isAfter(timestamp.plusSeconds(EXPIRATION_DURATION_SECONDS));
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES) // Run every 1 minute
    public void cleanExpiredNonces() {
        nonceStore.entrySet().removeIf(entry -> {
            Instant timestamp = entry.getValue();
            return timestamp.isAfter(timestamp.plusSeconds(60L * 10L));
        });
    }
}
