package com.szlazakm.chatserver.repositories;

import com.szlazakm.chatserver.entities.OPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OPKRepository extends JpaRepository<OPK, UUID> {
}
