package com.szlazakm.ChatServer.controllers

import com.szlazakm.ChatServer.helpers.TestUserProvider
import com.szlazakm.chatserver.controllers.UserController
import com.szlazakm.chatserver.dtos.VerifyPhoneNumberDTO
import com.szlazakm.chatserver.repositories.UserRepository
import com.szlazakm.chatserver.services.NonceService
import com.szlazakm.chatserver.services.UserService
import com.szlazakm.chatserver.utils.SignatureVerifier
import spock.lang.Specification

class UserControllerSpec extends Specification {

    def userService = Mock(UserService)
    def userRepository = Mock(UserRepository)
    def signatureVerifier = new SignatureVerifier()
    def nonceService = new NonceService(signatureVerifier, userRepository)
    def userController = new UserController(userService, nonceService)

    def "should call service user create on POST user"() {

        given:
        def createUserDto = TestUserProvider.createUserCreateDTO()

        when:
        userController.createUser(createUserDto)

        then:
        1 * userService.createUser(createUserDto)
    }

    def "should call getUserByPhoneNumber on GET user"() {

        given:
        def phoneNumber = "123123123"

        when:
        userController.getUser(phoneNumber)

        then:
        1 * userService.getUserByPhoneNumber(phoneNumber)
    }

    def "should call getKeyBundle on GET keyBundle"() {

        given:
        def phoneNumber = "123123123"

        when:
        userController.getKeyBundle(phoneNumber)

        then:
        1 * userService.getKeyBundle(phoneNumber)
    }

    def "should call verifyPhoneNumber on POST verifyPhoneNumber"() {

        given:
        def dto = VerifyPhoneNumberDTO.builder()
                .code("1234")
                .build()

        when:
        userController.verifyPhoneNumber(dto)

        then:
        1 * userService.verifyPhoneNumber(dto)
    }

}
