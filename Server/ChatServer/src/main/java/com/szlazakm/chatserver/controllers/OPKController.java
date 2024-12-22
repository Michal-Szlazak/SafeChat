package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.GetOpksDTO;
import com.szlazakm.chatserver.dtos.OPKsCreateDTO;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UnverifiedUserException;
import com.szlazakm.chatserver.services.NonceService;
import com.szlazakm.chatserver.services.OPKService;
import com.szlazakm.chatserver.utils.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.whispersystems.libsignal.logging.Log;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OPKController {

    private final OPKService opkService;
    private final NonceService nonceService;
    private final AuthHelper authHelper;

    @PostMapping("/opk")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOPKs(@RequestBody OPKsCreateDTO opksCreateDTO) {

        boolean isUserVerified = authHelper.verifyUser(opksCreateDTO.getPhoneNumber());

        if(!isUserVerified) {
            throw new UnverifiedUserException();
        }

        Log.d("OPKController", "createOPKs called.");
        nonceService.handleAuthMessage(
                opksCreateDTO.getPhoneNumber(),
                opksCreateDTO.getNonce(),
                opksCreateDTO.getNonceTimestamp(),
                opksCreateDTO.getAuthMessageSignature()
        );

        opkService.createOPK(opksCreateDTO);
    }

    @PostMapping("/opks")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> getOPKs(@RequestBody GetOpksDTO getOpksDTO) {

        boolean isUserVerified = authHelper.verifyUser(getOpksDTO.getPhoneNumber());

        if(!isUserVerified) {
            throw new UnverifiedUserException();
        }

        Log.d("OPKController", "Get opks called for number: " + getOpksDTO.getPhoneNumber());
        nonceService.handleAuthMessage(
                getOpksDTO.getPhoneNumber(),
                getOpksDTO.getNonce(),
                getOpksDTO.getNonceTimestamp(),
                getOpksDTO.getAuthMessageSignature()
        );

        return opkService.getOPKsIds(getOpksDTO.getPhoneNumber());
    }
}
