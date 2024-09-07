package com.szlazakm.ChatServer.services

import com.szlazakm.ChatServer.helpers.TestKeyBundleProvider
import com.szlazakm.ChatServer.helpers.TestOpkProvider
import com.szlazakm.ChatServer.helpers.TestSpkProvider
import com.szlazakm.ChatServer.helpers.TestUserProvider
import com.szlazakm.chatserver.entities.OPK
import com.szlazakm.chatserver.entities.User
import com.szlazakm.chatserver.exceptionHandling.exceptions.SPKNotFoundException
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException
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
        def createUserDTO = TestUserProvider.createUserCreateDTO()
        def encodedPin = "encodedPin"
        def userId = UUID.randomUUID()
        passwordEncoder.encode(createUserDTO.pin) >> encodedPin
        userRepository.save(_ as User) >> TestUserProvider.createUserFromUserCreateDTO(
                createUserDTO,
                userId,
                encodedPin
        )

        when:
        def returnUserId = userService.createUser(createUserDTO)

        then:
        returnUserId == userId
    }

    def "should return user by phone number when user exists"() {

        given:
        def phoneNumber = "123123123"
        def testUser = TestUserProvider.createUserEntity()
        userRepository.findByPhoneNumber(phoneNumber) >> Optional.of(testUser)

        when:
        def returnUser = userService.getUserByPhoneNumber(phoneNumber)

        then:
        def expected = TestUserProvider.createUserDTOFromUserEntity(testUser)
        returnUser == expected
    }

    def "should throw userNotFound exception when user not exists"() {

        given:
        def phoneNumber = "123123123"
        userRepository.findByPhoneNumber(phoneNumber) >> Optional.empty()

        when:
        userService.getUserByPhoneNumber(phoneNumber)

        then:
        thrown(UserNotFoundException)
    }

    def "should return keyBundle when user exists and spk exists and user has opk"() {

        given:
        def phoneNumber = "123123123"
        def testUser = TestUserProvider.createUserEntity()
        def testOpk = TestOpkProvider.createOpkForUser(testUser)
        def opkList = new ArrayList()
        opkList.add(testOpk)
        testUser.setOPKS(opkList)

        def testSPK = TestSpkProvider.createSPK()
        userRepository.findByPhoneNumber(phoneNumber) >> Optional.of(testUser)
        spkRepository.findByUserPhoneNumber(phoneNumber) >> Optional.of(testSPK)

        when:
        def resultKeyBundle = userService.getKeyBundle(phoneNumber)

        then:
        def expectedKeyBundle = TestKeyBundleProvider.getKeyBundle(testUser, testSPK, testOpk)
        resultKeyBundle == expectedKeyBundle
    }

    def "should return keyBundle when user exists and spk exists and user has no opk"() {

        given:
        def phoneNumber = "123123123"
        def testUser = TestUserProvider.createUserEntity()
        testUser.setOPKS(new ArrayList<OPK>())

        def testSPK = TestSpkProvider.createSPK()
        userRepository.findByPhoneNumber(phoneNumber) >> Optional.of(testUser)
        spkRepository.findByUserPhoneNumber(phoneNumber) >> Optional.of(testSPK)

        when:
        def resultKeyBundle = userService.getKeyBundle(phoneNumber)

        then:
        def expectedKeyBundle = TestKeyBundleProvider.getKeyBundleWithNullOpk(testUser, testSPK)
        resultKeyBundle == expectedKeyBundle
    }

    def "getKeyBundle should throw userNotFound exception when user not exists"() {

        given:
        def phoneNumber = "123123123"
        def testUser = TestUserProvider.createUserEntity()
        userRepository.findByPhoneNumber(phoneNumber) >> Optional.empty()

        when:
        userService.getKeyBundle(phoneNumber)

        then:
        thrown(UserNotFoundException)
    }

    def "getKeyBundle should throw userNotFound exception when user not exists"() {

        given:
        def phoneNumber = "123123123"
        def testUser = TestUserProvider.createUserEntity()
        userRepository.findByPhoneNumber(phoneNumber) >> Optional.of(testUser)
        spkRepository.findByUserPhoneNumber(phoneNumber) >> Optional.empty()

        when:
        userService.getKeyBundle(phoneNumber)

        then:
        thrown(SPKNotFoundException)
    }
}
