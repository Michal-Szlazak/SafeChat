package com.szlazakm.chatserver.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class OPK {

    @Id
    @GeneratedValue
    public UUID id;

    public String onetimePreKey;
}
