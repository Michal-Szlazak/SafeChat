package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.OPKCreateDTO;
import com.szlazakm.chatserver.services.OPKService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;

@RestController
@RequestMapping("/api/opk")
@RequiredArgsConstructor
public class OPKController {

    private final OPKService OPKService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOPK(OPKCreateDTO opkCreateDTO) throws SignatureException {

        OPKService.createOPK(opkCreateDTO);
    }
}
