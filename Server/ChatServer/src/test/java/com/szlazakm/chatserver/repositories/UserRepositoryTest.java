package com.szlazakm.chatserver.repositories;

import com.szlazakm.chatserver.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.Assert;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenUser_whenFindUserByPhoneNumber_thenReturnUser() {

        // Given
        User user1 = User.builder()
                .phoneNumber("123123123")
                .identityKey("identityKey1")
                .firstName("John")
                .lastName("Doe")
                .build();

        User user2 = User.builder()
                .phoneNumber("234234234")
                .identityKey("identityKey2")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // When
        Optional<User> resultUser = userRepository.findByPhoneNumber("123123123");

        // Then
        Assertions.assertTrue(resultUser.isPresent(), "User should be present");
        Assertions.assertEquals(user1, resultUser.get(), "The found user should match the user1 object");
    }

}