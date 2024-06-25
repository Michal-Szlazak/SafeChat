package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.OPKsCreateDTO;
import com.szlazakm.chatserver.services.OPKService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.whispersystems.libsignal.logging.Log;

import java.util.List;

@RestController
@RequestMapping("/api/opk")
@RequiredArgsConstructor
public class OPKController {

    private final OPKService opkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOPKs(@RequestBody OPKsCreateDTO opksCreateDTO) {
        Log.d("OPKController", "createOPKs called.");
        opkService.createOPK(opksCreateDTO);
    }

    @GetMapping("/{phoneNumber}")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> getOPKs(@PathVariable String phoneNumber) {
        Log.d("OPKController", "Get opks called for number: " + phoneNumber);
        return opkService.getOPKsIds(phoneNumber);
    }
}
