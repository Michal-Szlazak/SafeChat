package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.utils.SignatureVerifier;
import com.szlazakm.chatserver.dtos.SPKCreateOrUpdateDTO;
import com.szlazakm.chatserver.entities.SPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.SignatureVerifierException;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.SPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SPKService {

    private final SPKRepository spkRepository;
    private final UserRepository userRepository;
    private final SignatureVerifier signatureVerifier;

    public void createOrUpdateSPK(SPKCreateOrUpdateDTO spkCreateOrUpdateDTO) throws SignatureException {

        Optional<User> optUser = userRepository.findByPhoneNumber(spkCreateOrUpdateDTO.getPhoneNumber());
        User user = optUser.orElseThrow(UserNotFoundException::new);
        Optional<SPK> optSpk = spkRepository.findByUserPhoneNumber(spkCreateOrUpdateDTO.getPhoneNumber());

        SPK spk;

        if(optSpk.isEmpty()) {
            spk = SPK.builder()
                    .keyId(spkCreateOrUpdateDTO.getId())
                    .signedPreKey(spkCreateOrUpdateDTO.getSignedPreKey())
                    .signature(spkCreateOrUpdateDTO.getSignature())
                    .timestamp(spkCreateOrUpdateDTO.getTimestamp())
                    .user(user)
                    .build();
        } else {

            spk = optSpk.get();
            spk.setKeyId(spkCreateOrUpdateDTO.getId());
            spk.setSignedPreKey(spkCreateOrUpdateDTO.getSignedPreKey());
            spk.setSignature(spkCreateOrUpdateDTO.getSignature());
            spk.setTimestamp(spkCreateOrUpdateDTO.getTimestamp());
        }

        boolean isSignatureVerified;

        try {
            isSignatureVerified = signatureVerifier.verifySignature(
                    user.getIdentityKey(),
                    spkCreateOrUpdateDTO.getSignedPreKey(),
                    spkCreateOrUpdateDTO.getSignature()
            );
        } catch (InvalidKeySpecException | InvalidKeyException e) {
            throw new SignatureVerifierException("Failed to verify the signed key. Reson: " + e.getMessage());
        }

        if(isSignatureVerified) {
            spkRepository.save(spk);
        } else {
            throw new SignatureVerifierException("Signature verification returned false.");
        }
    }
}
