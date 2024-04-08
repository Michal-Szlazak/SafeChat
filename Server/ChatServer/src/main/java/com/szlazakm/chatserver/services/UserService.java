package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.auth.SignatureVerifier;
import com.szlazakm.chatserver.dtos.SPKCreateDTO;
import com.szlazakm.chatserver.dtos.UserCreateDTO;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.mappers.UserMapper;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SignatureVerifier signatureVerifier;

    public void createUser(UserCreateDTO userCreateDTO) {
        userRepository.save(userMapper.toEntity(userCreateDTO));
    }

    public void createSPK(SPKCreateDTO spkCreateDTO) throws SignatureException {

        Optional<User> optionalUser = userRepository.findById(spkCreateDTO.getUserId());

        User user = optionalUser.orElseThrow(UserNotFoundException::new);

        String signedPreKey = spkCreateDTO.getSignedPreKey();
        String signature = spkCreateDTO.getSignature();

        boolean isSignatureVerified = signatureVerifier.verify(
                signedPreKey,
                signature,
                user.getIdentityKey()
        );

        if(isSignatureVerified) {
            user.setSignedPreKey(signedPreKey);
            user.setSignature(signature);
        } else {
            throw new SignatureException("Signature verification returned false.");
        }
    }

}
