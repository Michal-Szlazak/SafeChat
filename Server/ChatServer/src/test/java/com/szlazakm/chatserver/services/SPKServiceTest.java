package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.SPKCreateOrUpdateDTO;
import com.szlazakm.chatserver.entities.SPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.SPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SPKServiceTest {

    @Mock
    private SPKRepository spkRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SPKService spkService;

    private User user;
    private SPK spk;

    @BeforeEach
    public void setup() {

        user = User.builder()
                .userId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .identityKey("identityKey")
                .pin("encodedPin")
                .OPKS(new ArrayList<>())
                .build();

        spk = SPK.builder()
                .user(user)
                .keyId(1)
                .signedPreKey("signed-pre-key")
                .signature("signature")
                .timestamp(1L)
                .databaseId(UUID.randomUUID())
                .build();

    }


    @Test
    void createOrUpdateSPK_ShouldCreateSPK_WhenSPKNotExist() throws SignatureException {

        // Arrange
        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(user));
        when(spkRepository.findByUserPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.empty());

        SPKCreateOrUpdateDTO spkCreateOrUpdateDTO = SPKCreateOrUpdateDTO.builder()
                .id(1)
                .phoneNumber(user.getPhoneNumber())
                .signedPreKey("signed-pre-key")
                .signature("signature")
                .timestamp(1L)
                .build();

        // Act
        spkService.createOrUpdateSPK(spkCreateOrUpdateDTO);

        // Assert
        verify(spkRepository, times(1)).save(any(SPK.class));
        assertEquals(spkCreateOrUpdateDTO.getId(), spk.getKeyId());
        assertEquals(spkCreateOrUpdateDTO.getSignedPreKey(), spk.getSignedPreKey());
        assertEquals(spkCreateOrUpdateDTO.getSignature(), spk.getSignature());
        assertEquals(spkCreateOrUpdateDTO.getTimestamp(), spk.getTimestamp());
    }

    @Test
    void createOrUpdateSPK_ShouldUpdateSPK_WhenSPKExist() throws SignatureException {

        // Arrange
        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(user));
        when(spkRepository.findByUserPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(spk));

        SPKCreateOrUpdateDTO spkCreateOrUpdateDTO = SPKCreateOrUpdateDTO.builder()
                .id(1)
                .phoneNumber(user.getPhoneNumber())
                .signedPreKey("signed-pre-key")
                .signature("signature")
                .timestamp(1L)
                .build();

        // Act
        spkService.createOrUpdateSPK(spkCreateOrUpdateDTO);

        // Assert
        verify(spkRepository, times(1)).save(any(SPK.class));
        assertEquals(spkCreateOrUpdateDTO.getId(), spk.getKeyId());
        assertEquals(spkCreateOrUpdateDTO.getSignedPreKey(), spk.getSignedPreKey());
        assertEquals(spkCreateOrUpdateDTO.getSignature(), spk.getSignature());
        assertEquals(spkCreateOrUpdateDTO.getTimestamp(), spk.getTimestamp());
    }

    @Test
    void createOrUpdateSPK_ShouldThrowUserNotFoundException_WhenUserNotExist() throws SignatureException {

        // Arrange
        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.empty());

        SPKCreateOrUpdateDTO spkCreateOrUpdateDTO = SPKCreateOrUpdateDTO.builder()
                .id(1)
                .phoneNumber(user.getPhoneNumber())
                .signedPreKey("signed-pre-key")
                .signature("signature")
                .timestamp(1L)
                .build();

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> spkService.createOrUpdateSPK(spkCreateOrUpdateDTO)
        );
    }
}