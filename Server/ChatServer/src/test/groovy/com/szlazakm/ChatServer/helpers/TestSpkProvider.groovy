package com.szlazakm.ChatServer.helpers

import com.szlazakm.chatserver.dtos.SPKCreateOrUpdateDTO
import com.szlazakm.chatserver.entities.SPK
import com.szlazakm.chatserver.entities.User

class TestSpkProvider {

    static SPK createSPK(User user) {
        SPK.builder()
            .databaseId(UUID.randomUUID())
            .keyId(1)
            .signedPreKey("signed-pk")
            .signature("signature")
            .timestamp(1L)
            .user(user)
            .build()
    }

    static createSpkCreateOrUpdateDTO() {
        SPKCreateOrUpdateDTO.builder()
            .id(1)
            .phoneNumber("123123123")
            .signedPreKey("preKey")
            .signature("signature")
            .timestamp(1L)
            .build()
    }
}
