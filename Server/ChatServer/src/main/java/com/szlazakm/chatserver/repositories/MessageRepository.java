package com.szlazakm.chatserver.repositories;

import com.szlazakm.chatserver.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> getAllByToPhoneNumber(String toPhoneNumber);
}
