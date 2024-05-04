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
public class SPK {

    @Id
    @GeneratedValue
    private UUID databaseId;
    private int keyId;
    private String signedPreKey;
    private String signature;
    private Long timestamp;

    @OneToOne
    @JoinColumn(name = "phone_number", referencedColumnName = "phone_number")
    private User user;
}
