package com.szlazakm.ChatServer.helpers

import com.szlazakm.chatserver.dtos.OPKCreateDTO
import com.szlazakm.chatserver.dtos.OPKsCreateDTO
import com.szlazakm.chatserver.entities.OPK
import com.szlazakm.chatserver.entities.User

class TestOpkProvider {

    static Random random = new Random()

    static OPK createOpkForUser(User user) {
        OPK.builder()
            .keyId(random.nextInt())
            .databaseId(random.nextInt())
            .preKey("preKey")
            .user(user)
            .build()
    }

    static OPKsCreateDTO createOpksCreateDTO(String phoneNumber) {

        def opks = new ArrayList<OPKCreateDTO>()
        opks.add(createOpkCreateDTO(phoneNumber))
        opks.add(createOpkCreateDTO(phoneNumber))

        OPKsCreateDTO.builder()
            .phoneNumber(phoneNumber)
            .opkCreateDTOs(opks)
            .build()
    }



    static OPKCreateDTO createOpkCreateDTO(String phoneNumber) {
        OPKCreateDTO.builder()
            .id(random.nextInt())
            .preKey("preKey")
            .build()
    }
}
