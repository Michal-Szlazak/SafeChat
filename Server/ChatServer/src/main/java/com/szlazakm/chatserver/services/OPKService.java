package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.OPKCreateDTO;
import com.szlazakm.chatserver.dtos.OPKsCreateDTO;
import com.szlazakm.chatserver.entities.OPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.OPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OPKService {

    private final UserRepository userRepository;
    private final OPKRepository opkRepository;

    public void createOPK(OPKsCreateDTO opksCreateDTO) {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(opksCreateDTO.getPhoneNumber());
        User user = optionalUser.orElseThrow(UserNotFoundException::new);

        List<OPK> newOpks = new ArrayList<>();

        for (OPKCreateDTO opkCreateDTO : opksCreateDTO.getOpkCreateDTOs()) {

            OPK opk = OPK.builder()
                    .keyId(opkCreateDTO.getId())
                    .preKey(opkCreateDTO.getPreKey())
                    .user(user)
                    .build();
            opkRepository.save(opk);

            newOpks.add(opk);
        }

        user.getOPKS().addAll(newOpks);
        userRepository.save(user);
    }

    public List<Integer> getOPKS(String phoneNumber) {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException(
                "User with phone " + phoneNumber + " not found. (OPK Service - getOpks)"
        ));
        
        List<OPK> opks = user.getOPKS();
        
        return opks.stream().map( u -> u.keyId).toList();
    }
}
