package com.szlazakm.chatserver.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder
@Jacksonized
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    UUID id;
    String from;
    String to;
    String text;
}
