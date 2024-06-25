package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.SPKCreateDTO;
import com.szlazakm.chatserver.services.SPKService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

@RestController
@RequestMapping("/api/spk")
@RequiredArgsConstructor
public class SPKController {

    private final SPKService spkService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addSpk(@RequestBody SPKCreateDTO spkCreateDTO) throws SignatureException {
        spkService.createSPK(spkCreateDTO);
    }

}
