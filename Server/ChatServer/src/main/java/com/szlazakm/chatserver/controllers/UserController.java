package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.*;
import com.szlazakm.chatserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

@RestController()
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserCreateDTO userCreateDTO) {
        userService.createUser(userCreateDTO);
    }

    @PostMapping("/spk")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSpk(@RequestBody SPKCreateDTO spkCreateDTO) throws SignatureException {
        userService.createSPK(spkCreateDTO);
    }

    @GetMapping
    public UserDTO getUser(@RequestParam String phoneNumber) {

        return userService.getUserByPhoneNumber(phoneNumber);
    }

    @GetMapping("/keyBundle")
    public KeyBundleDTO getKeyBundle(@RequestParam String phoneNumber) {
        return userService.getKeyBundle(phoneNumber);
    }

    @PostMapping("/verify")
    public boolean verifyPhoneNumber(@RequestBody VerifyPhoneNumberDTO verifyPhoneNumberDTO) {
        return userService.verifyPhoneNumber(verifyPhoneNumberDTO);
    }
}
