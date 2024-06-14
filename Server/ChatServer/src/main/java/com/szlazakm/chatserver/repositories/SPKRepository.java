package com.szlazakm.chatserver.repositories;

import com.szlazakm.chatserver.entities.SPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SPKRepository extends JpaRepository<SPK, UUID> {

    Optional<SPK> findByUserPhoneNumber(String phoneNumber);
}
