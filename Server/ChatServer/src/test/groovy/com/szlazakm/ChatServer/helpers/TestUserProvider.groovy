package com.szlazakm.ChatServer.helpers

import com.szlazakm.chatserver.dtos.UserCreateDTO
import com.szlazakm.chatserver.entities.User

class TestUserProvider {

    static UserCreateDTO createUserCreateDTO() {
        UserCreateDTO.builder()
            .firstName("exampleName")
            .lastName("exampleLastName")
            .phoneNumber("123123123")
            .identityKey("exampleIdKey")
            .pin("1234")
            .build()
    }

    static User createUserFromUserCreateDTO(UserCreateDTO userCreateDTO, UUID id, String encodedPin) {
        User.builder()
                .userId(id)
                .firstName(userCreateDTO.getFirstName())
                .lastName(userCreateDTO.getLastName())
                .phoneNumber(userCreateDTO.getPhoneNumber())
                .identityKey(userCreateDTO.getIdentityKey())
                .pin(encodedPin)
                .build();
    }
}
