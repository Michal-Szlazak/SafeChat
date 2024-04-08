package com.szlazakm.chatserver.mappers;

import com.szlazakm.chatserver.dtos.OPKCreateDTO;
import com.szlazakm.chatserver.entities.OPK;
import org.mapstruct.Mapper;

@Mapper
public interface OPKMapper {

    OPK toEntity(OPKCreateDTO opkCreateDTO);
}
