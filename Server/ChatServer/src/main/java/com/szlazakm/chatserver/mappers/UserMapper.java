package com.szlazakm.chatserver.mappers;

import com.szlazakm.chatserver.dtos.UserCreateDTO;
import com.szlazakm.chatserver.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "userId", ignore = true)
    User toEntity(UserCreateDTO userCreateDTO);
}
