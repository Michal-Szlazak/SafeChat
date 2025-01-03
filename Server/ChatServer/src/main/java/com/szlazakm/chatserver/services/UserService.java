package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.*;
import com.szlazakm.chatserver.dtos.response.KeyBundleDTO;
import com.szlazakm.chatserver.dtos.response.UserDTO;
import com.szlazakm.chatserver.entities.OPK;
import com.szlazakm.chatserver.entities.SPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.SPKNotFoundException;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.SPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SPKRepository spkRepository;
    private final PasswordEncoder passwordEncoder;

    public UUID createUser(UserCreateDTO userCreateDTO) {

        String encodedPin = passwordEncoder.encode(userCreateDTO.getPin());

        User user = User.builder()
                .firstName(userCreateDTO.getFirstName())
                .lastName(userCreateDTO.getLastName())
                .phoneNumber(userCreateDTO.getPhoneNumber())
                .identityKey(userCreateDTO.getIdentityKey())
                .pin(encodedPin)
                .build();

        User savedUser = userRepository.save(user);

        return savedUser.getUserId();
    }

    public UserDTO getUserByPhoneNumber(String phoneNumber) {

        Optional<User> optUser = userRepository.findByPhoneNumber(phoneNumber);
        User user = optUser.orElseThrow(UserNotFoundException::new);

        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public KeyBundleDTO getKeyBundle(String phoneNumber) {

        Optional<User> optUser = userRepository.findByPhoneNumber(phoneNumber);
        Optional<SPK> optSpk = spkRepository.findByUserPhoneNumber(phoneNumber);
        User user = optUser.orElseThrow(UserNotFoundException::new);
        SPK spk = optSpk.orElseThrow(SPKNotFoundException::new);
        List<OPK> opkList = user.getOPKS();

        OPK opk = null;
        Integer opkKeyId = null;
        String opkPreKey = null;

        if(!opkList.isEmpty()) {
            opk = opkList.get(0);
            opkList.remove(0);
            opkKeyId = opk.keyId;
            opkPreKey = opk.preKey;
        }

        userRepository.save(user);

        return KeyBundleDTO.builder()
                .identityKey(user.getIdentityKey())
                .signedPreKeyId(spk.getKeyId())
                .signedPreKey(spk.getSignedPreKey())
                .signature(spk.getSignature())
                .onetimePreKeyId(opkKeyId)
                .onetimePreKey(opkPreKey)
                .build();
    }

    public boolean verifyPhoneNumber(VerifyPhoneNumberDTO verifyPhoneNumberDTO) {

        Optional<User> optUser = userRepository.findByPhoneNumber(verifyPhoneNumberDTO.getPhoneNumber());
        User user = optUser.orElseThrow(UserNotFoundException::new);
        user.setVerified(true);
        userRepository.save(user);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //TODO - code verification
        return true;
    }
}
