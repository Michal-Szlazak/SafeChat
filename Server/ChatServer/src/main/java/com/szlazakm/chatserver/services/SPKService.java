package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.SPKCreateDTO;
import com.szlazakm.chatserver.entities.SPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.SPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SPKService {

    private final SPKRepository spkRepository;
    private final UserRepository userRepository;

    public void createSPK(SPKCreateDTO spkCreateDTO) throws SignatureException {

        Optional<User> optUser = userRepository.findByPhoneNumber(spkCreateDTO.getPhoneNumber());
        User user = optUser.orElseThrow(UserNotFoundException::new);
        Optional<SPK> optSpk = spkRepository.findByUserPhoneNumber(spkCreateDTO.getPhoneNumber());

        SPK spk;

        if(optSpk.isEmpty()) {
            spk = SPK.builder()
                    .keyId(spkCreateDTO.getId())
                    .signedPreKey(spkCreateDTO.getSignedPreKey())
                    .signature(spkCreateDTO.getSignature())
                    .timestamp(spkCreateDTO.getTimestamp())
                    .user(user)
                    .build();
        } else {

            spk = optSpk.get();
            spk.setKeyId(spkCreateDTO.getId());
            spk.setSignedPreKey(spkCreateDTO.getSignedPreKey());
            spk.setSignature(spkCreateDTO.getSignature());
            spk.setTimestamp(spkCreateDTO.getTimestamp());
        }

        boolean isSignatureVerified = true; //TODO Verify the signature

        if(isSignatureVerified) {
            spkRepository.save(spk);
        } else {
            throw new SignatureException("Signature verification returned false.");
        }
    }
}
