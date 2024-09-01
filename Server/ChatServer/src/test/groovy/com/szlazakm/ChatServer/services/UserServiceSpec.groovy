package com.szlazakm.ChatServer.services

import com.szlazakm.ChatServer.helpers.TestUserProvider
import com.szlazakm.chatserver.entities.User
import com.szlazakm.chatserver.repositories.SPKRepository
import com.szlazakm.chatserver.repositories.UserRepository
import com.szlazakm.chatserver.services.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceSpec extends Specification{

    def userRepository = Mock(UserRepository)
    def spkRepository = Mock(SPKRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def userService = new UserService(
            userRepository,
            spkRepository,
            passwordEncoder
    )

    def "should return userId when user created"() {

        given:
        def userDTO = TestUserProvider.createUserCreateDTO()
        def encodedPin = "encodedPin"
        def userId = UUID.randomUUID()
        passwordEncoder.encode(userDTO.pin) >> encodedPin
        userRepository.save(_ as User) >> TestUserProvider.createUserFromUserCreateDTO(
                userDTO,
                userId,
                encodedPin
        )

        when:
        def returnUserId = userService.createUser(userDTO)

        then:
        returnUserId == userId
    }

}
