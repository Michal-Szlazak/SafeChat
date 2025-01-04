package com.szlazakm.chatserver.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBController {

    @Value("${SAFECHAT_DB_USERNAME}")
    private String username;

    @Value("${SAFECHAT_DB_PASS}")
    private String password;

    @Value("${SAFECHAT_DB_ADDRESS}")
    private String address;

    @GetMapping("/postgres")
    public String getDbData() {

        return "Username: " + username + ", password: " + password + ", address: " + address;
    }

}
