package com.szlazakm.chatserver.dtos.response;

import com.szlazakm.chatserver.dtos.EncryptedMessageDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class OutputEncryptedMessageDTO extends EncryptedMessageDTO {

    String date;
}
