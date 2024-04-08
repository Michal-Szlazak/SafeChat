package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.SPKCreateDTO;
import com.szlazakm.chatserver.dtos.UserCreateDTO;
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
    public void createUser(UserCreateDTO userCreateDTO) {
        userService.createUser(userCreateDTO);
    }

    @PostMapping("/spk")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSpk(SPKCreateDTO spkCreateDTO) throws SignatureException {
        userService.createSPK(spkCreateDTO);
    }
}
