package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.SPKCreateOrUpdateDTO;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UnverifiedUserException;
import com.szlazakm.chatserver.services.NonceService;
import com.szlazakm.chatserver.services.SPKService;
import com.szlazakm.chatserver.utils.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.whispersystems.libsignal.logging.Log;

import java.security.SignatureException;
import java.util.Base64;

@RestController
@RequestMapping("/api/spk")
@RequiredArgsConstructor
public class SPKController {

    private final SPKService spkService;
    private final NonceService nonceService;
    private final AuthHelper authHelper;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addSpk(@RequestBody SPKCreateOrUpdateDTO spkCreateOrUpdateDTO) throws SignatureException {

        Log.d("SPKController", "addSpk called.");

        boolean isUserVerified = authHelper.verifyUser(spkCreateOrUpdateDTO.getPhoneNumber());

        if(!isUserVerified) {
            throw new UnverifiedUserException();
        }

        nonceService.handleAuthMessage(
                spkCreateOrUpdateDTO.getPhoneNumber(),
                spkCreateOrUpdateDTO.getNonce(),
                spkCreateOrUpdateDTO.getNonceTimestamp(),
                spkCreateOrUpdateDTO.getAuthMessageSignature()
        );

        spkService.createOrUpdateSPK(spkCreateOrUpdateDTO);
    }

//    {"initial":true,"id":"957956ad-7c33-41f1-9502-bf9c05ff023a","from":"111111111","to":"999999999","cipher":"Kp+RpkgVAjCH1MfvuAKOrI7VdP4yvIQ2od6LuOtDhmSZYOVUTbWPuw==","aliceIdentityPublicKey":"MFUwEwYHKoZIzj0CAQYIKoZIzj0DAQQDPgAEHSqvk3TkTTOXIhYqb0mkHv6usFfq849EeS3C3CRvH2Sb9HMNJ5ZsMBAl1Am4uq8YFGTZArfgdNE/eD0J","aliceEphemeralPublicKey":"MFUwEwYHKoZIzj0CAQYIKoZIzj0DAQQDPgAEPpxmAXaCQp1yD2O5Gdfc/vYboqxrdEtCOZxcLBqeeb/VeMI8/RrqBj38yGRp9TaQjh2WG5Ouh1e89IyP","bobOpkId":1,"bobSpkId":1931376600,"ephemeralRatchetKey":"MFUwEwYHKoZIzj0CAQYIKoZIzj0DAQQDPgAEMoyL+Hn4LX/eZuEU6y2l5SrjAzGf7H9Xhq2IvUqGN2u0FspV27eML8BXIqQHsxW+20gQLeb5ESlI8eHZ","messageIndex":0,"lastMessageBatchSize":0,"phoneNumber":null,"nonceTimestamp":null,"nonce":null,"authMessageSignature":null,"date":"2024-12-21 13:46:12:960"}

}
