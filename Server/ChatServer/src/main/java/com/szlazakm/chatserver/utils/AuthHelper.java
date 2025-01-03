package com.szlazakm.chatserver.utils;

import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final UserRepository userRepository;

    public boolean verifyUser(String phoneNumber) {

        Optional<User> optUser = userRepository.findByPhoneNumber(phoneNumber);
        User user =  optUser.orElseThrow(UserNotFoundException::new);

        return user.isVerified();
    }
}
