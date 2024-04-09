package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.auth.SignatureVerifier;
import com.szlazakm.chatserver.dtos.*;
import com.szlazakm.chatserver.entities.OPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.mappers.UserMapper;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.List;
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

    public UserDTO getUserByPhoneNumber(String phoneNumber) {

        Optional<User> optUser = userRepository.getUserByPhoneNumber(phoneNumber);
        User user = optUser.orElseThrow(UserNotFoundException::new);
        return UserDTO.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public KeyBundleDTO getKeyBundle(KeyBundleGetDTO keyBundleGetDTO) {

        Optional<User> optUser = userRepository.findById(keyBundleGetDTO.getUserId());
        User user = optUser.orElseThrow(UserNotFoundException::new);
        List<OPK> opkList = user.getOPKS();

        String opk = "";

        if(!opkList.isEmpty()) {
            opk = opkList.get(0).onetimePreKey;
            opkList.remove(0);
        }

        return KeyBundleDTO.builder()
                .identityKey(user.getIdentityKey())
                .signedPreKey(user.getSignedPreKey())
                .signature(user.getSignature())
                .onetimePreKey(opk)
                .build();
    }
}
