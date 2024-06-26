package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.OPKCreateDTO;
import com.szlazakm.chatserver.dtos.OPKsCreateDTO;
import com.szlazakm.chatserver.entities.OPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.OPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OPKServiceTest {

    @Mock
    private OPKRepository opkRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private OPKService opkService;

    private User user;
    private List<OPKCreateDTO> opkCreateDTOS;

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

        OPKCreateDTO opkCreateDTO1 = OPKCreateDTO.builder()
                .id(1)
                .preKey("pre-key-1")
                .build();

        OPKCreateDTO opkCreateDTO2 = OPKCreateDTO.builder()
                .id(2)
                .preKey("pre-key-2")
                .build();

        opkCreateDTOS = List.of(opkCreateDTO1, opkCreateDTO2);
    }

    @Test
    void createOPK_ShouldCreateOPK_WhenCorrectDTOProvided() {

        // Arrange
        OPKsCreateDTO opKsCreateDTO = OPKsCreateDTO.builder()
                .phoneNumber(user.getPhoneNumber())
                .opkCreateDTOs(opkCreateDTOS)
                .build();

        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(user));

        // Act
        opkService.createOPK(opKsCreateDTO);

        // Assert
        verify(opkRepository, times(opkCreateDTOS.size())).save(any(OPK.class));
    }

    @Test
    void createOPK_ShouldThrowUserNotFound_WhenUserNotExist() {

        // Arrange
        String randomPhoneNumber = "123321123";
        OPKsCreateDTO opKsCreateDTO = OPKsCreateDTO.builder()
                .phoneNumber(randomPhoneNumber)
                .opkCreateDTOs(opkCreateDTOS)
                .build();

        when(userRepository.findByPhoneNumber(randomPhoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> opkService.createOPK(opKsCreateDTO));
    }

    @Test
    void getOPKsIds_ShouldReturnOpks_WhenUserExists() {

        // Arrange

        OPK opk1 = OPK.builder()
                .user(user)
                .keyId(1)
                .databaseId(1)
                .preKey("opk-1")
                .build();

        OPK opk2 = OPK.builder()
                .user(user)
                .keyId(2)
                .databaseId(2)
                .preKey("opk-2")
                .build();

        List<OPK> opks = List.of(opk1, opk2);

        user.setOPKS(opks);

        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(user));

        // Act
        List<Integer> opkIds = opkService.getOPKsIds(user.getPhoneNumber());

        // Assert
        assertEquals(opks.stream().map(OPK::getKeyId).toList(), opkIds);
    }

    @Test
    void getOPKsIds_ShouldThrowUserNotFound_WhenUserNotExist() {

        // Arrange
        String randomPhoneNumber = "123321123";
        when(userRepository.findByPhoneNumber(randomPhoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> opkService.getOPKsIds(randomPhoneNumber));
    }
}