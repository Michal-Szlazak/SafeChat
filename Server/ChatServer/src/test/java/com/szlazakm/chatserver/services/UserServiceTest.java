package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.KeyBundleDTO;
import com.szlazakm.chatserver.dtos.UserCreateDTO;
import com.szlazakm.chatserver.dtos.UserDTO;
import com.szlazakm.chatserver.entities.OPK;
import com.szlazakm.chatserver.entities.SPK;
import com.szlazakm.chatserver.entities.User;
import com.szlazakm.chatserver.exceptionHandling.exceptions.SPKNotFoundException;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException;
import com.szlazakm.chatserver.repositories.SPKRepository;
import com.szlazakm.chatserver.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SPKRepository spkRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateDTO userCreateDTO;
    private User user;
    private SPK spk;

    @BeforeEach
    public void setup() {

        userCreateDTO = UserCreateDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123123123")
                .pin("1234")
                .identityKey("id-key-jd")
                .build();

        user = User.builder()
                .userId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .identityKey("identityKey")
                .pin("encodedPin")
                .build();

        spk = SPK.builder()
                .user(user)
                .keyId(1)
                .signedPreKey("signed-pre-key")
                .signature("signature")
                .timestamp(1L)
                .databaseId(UUID.randomUUID())
                .build();

        OPK opk1 = OPK.builder()
                .keyId(1)
                .databaseId(1)
                .preKey("pre-key-1")
                .user(user)
                .build();

        OPK opk2 = OPK.builder()
                .keyId(2)
                .databaseId(2)
                .preKey("pre-key-2")
                .user(user)
                .build();

        List<OPK> opks = new ArrayList<>(List.of(opk1, opk2));

        user.setOPKS(opks);
    }

    @Test
    void createUser_ShouldReturnUserResponseDTO_WhenValidUserCreateDTOProvided() {
        // Arrange
        when(passwordEncoder.encode(userCreateDTO.getPin())).thenReturn("encodedPin");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UUID result = userService.createUser(userCreateDTO);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(user.getUserId(), result, "The returned UUID should match the saved user's UUID");
    }

    @Test
    void getUserByPhoneNumber_ShouldReturnUserDTO_WhenUserExists() {

        // Arrange
        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(user));
        UserDTO expectedUserDTO = UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();

        // Act
        UserDTO resultUserDTO = userService.getUserByPhoneNumber(user.getPhoneNumber());

        // Assert
        assertNotNull(resultUserDTO);
        assertEquals(expectedUserDTO.getFirstName(), resultUserDTO.getFirstName());
        assertEquals(expectedUserDTO.getLastName(), resultUserDTO.getLastName());
        assertEquals(expectedUserDTO.getPhoneNumber(), resultUserDTO.getPhoneNumber());

        verify(userRepository).findByPhoneNumber(user.getPhoneNumber());
    }


    @Test
    void getUserByPhoneNumber_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        String nonExistentPhoneNumber = "1234567890";
        when(userRepository.findByPhoneNumber(nonExistentPhoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByPhoneNumber(nonExistentPhoneNumber);
        });

        // Verify
        verify(userRepository).findByPhoneNumber(nonExistentPhoneNumber);
    }

    @Test
    void getKeyBundle_ShouldReturnKeyBundle_WhenUserWithPhoneNumberExists() {

        // Arrange
        List<OPK> opks = user.getOPKS();

        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(user));
        when(spkRepository.findByUserPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(spk));
        KeyBundleDTO excpectedKeyBundleDTO = KeyBundleDTO.builder()
                .identityKey(user.getIdentityKey())
                .signedPreKeyId(spk.getKeyId())
                .signedPreKey(spk.getSignedPreKey())
                .signature(spk.getSignature())
                .onetimePreKeyId(opks.get(0).getKeyId())
                .onetimePreKey(opks.get(0).getPreKey())
                .build();

        // Act
        KeyBundleDTO keyBundleDTO = userService.getKeyBundle(user.getPhoneNumber());

        // Assert
        assertNotNull(keyBundleDTO);
        assertEquals(excpectedKeyBundleDTO.getIdentityKey(), keyBundleDTO.getIdentityKey());
        assertEquals(excpectedKeyBundleDTO.getSignedPreKeyId(), keyBundleDTO.getSignedPreKeyId());
        assertEquals(excpectedKeyBundleDTO.getSignedPreKey(), keyBundleDTO.getSignedPreKey());
        assertEquals(excpectedKeyBundleDTO.getSignature(), keyBundleDTO.getSignature());
        assertEquals(excpectedKeyBundleDTO.getOnetimePreKeyId(), keyBundleDTO.getOnetimePreKeyId());
        assertEquals(excpectedKeyBundleDTO.getOnetimePreKey(), keyBundleDTO.getOnetimePreKey());
    }

    @Test
    void getKeyBundle_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {

        // Arrange
        String randomPhoneNumber = "321321321";
        when(userRepository.findByPhoneNumber(randomPhoneNumber)).thenReturn(Optional.empty());
        when(spkRepository.findByUserPhoneNumber(randomPhoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getKeyBundle(randomPhoneNumber));
    }

    @Test
    void getKeyBundle_ShouldThrowSPKNotFoundException_WhenSPKDoesNotExist() {

        // Arrange
        String randomPhoneNumber = "321321321";
        when(userRepository.findByPhoneNumber(randomPhoneNumber)).thenReturn(Optional.of(user));
        when(spkRepository.findByUserPhoneNumber(randomPhoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SPKNotFoundException.class, () -> userService.getKeyBundle(randomPhoneNumber));
    }
}