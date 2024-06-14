package com.szlazakm.chatserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue()
    private UUID messageId;
    private boolean isInitial;
    private String fromPhoneNumber;
    private String toPhoneNumber;
    private String cipher;
    private String aliceIdentityPublicKey;
    private String aliceEphemeralPublicKey;
    private Integer bobOpkId;
    private Integer bobSpkId;
    private String timestamp;
}
