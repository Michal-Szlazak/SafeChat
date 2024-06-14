package com.szlazakm.chatserver.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
public class OPK {

    @Id
    @GeneratedValue
    public int databaseId;
    public int keyId;
    public String preKey;

    @ManyToOne
    private User user;
}
