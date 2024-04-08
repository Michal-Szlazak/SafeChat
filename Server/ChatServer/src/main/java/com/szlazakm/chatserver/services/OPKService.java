package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.auth.SignatureVerifier;
import com.szlazakm.chatserver.dtos.OPKCreateDTO;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.mappers.OPKMapper;
import com.szlazakm.chatserver.repositories.OPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OPKService {

    private final UserRepository userRepository;
    private final OPKRepository OPKRepository;
    private final OPKMapper OPKMapper;
    private final SignatureVerifier signatureVerifier;

    public void createOPK(OPKCreateDTO opkCreateDTO) throws SignatureException {

        Optional<User> optionalUser = userRepository.findById(opkCreateDTO.getUserId());
        User user = optionalUser.orElseThrow(UserNotFoundException::new);

        String signedOPK = opkCreateDTO.getSignedOnetimePreKey();
        String signature = opkCreateDTO.getSignature();
        String publicKey = user.getIdentityKey();

        boolean isSignatureVerified = signatureVerifier.verify(
                signedOPK,
                signature,
                user.getIdentityKey()
        );

        if(isSignatureVerified) {
            OPKRepository.save(OPKMapper.toEntity(opkCreateDTO));
        } else {
            throw new SignatureException("Signature verification returned false.");
        }
    }
}
