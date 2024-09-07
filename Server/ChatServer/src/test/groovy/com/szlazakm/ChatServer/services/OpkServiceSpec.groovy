package com.szlazakm.ChatServer.services

import com.szlazakm.ChatServer.helpers.TestOpkProvider
import com.szlazakm.ChatServer.helpers.TestUserProvider
import com.szlazakm.chatserver.dtos.OPKCreateDTO
import com.szlazakm.chatserver.entities.OPK
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException
import com.szlazakm.chatserver.repositories.OPKRepository
import com.szlazakm.chatserver.repositories.UserRepository
import com.szlazakm.chatserver.services.OPKService
import spock.lang.Specification

class OpkServiceSpec extends Specification{

    def userRepository = Mock(UserRepository)
    def opkRepository = Mock(OPKRepository)
    def opkService = new OPKService(userRepository, opkRepository)

    def "should create opk when user exists"() {

        given:
        def testUser = TestUserProvider.createUserEntity()
        testUser.setOPKS(new ArrayList<OPK>())

        def opksCreateDTO = TestOpkProvider.createOpksCreateDTO(testUser.getPhoneNumber())
        userRepository.findByPhoneNumber(opksCreateDTO.getPhoneNumber()) >> Optional.of(testUser)
        def expectedOpkList = new ArrayList<OPK>()

        when:
        opkService.createOPK(opksCreateDTO)

        then:

        for(OPKCreateDTO opkCreateDTO : opksCreateDTO.getOpkCreateDTOs()) {

            def expectedOPK = OPK.builder()
                    .keyId(opkCreateDTO.getId())
                    .preKey(opkCreateDTO.getPreKey())
                    .user(testUser)
                    .build();

            1 * opkRepository.save(expectedOPK)

            expectedOpkList.add(expectedOPK)
        }

        1 * userRepository.save(testUser)
    }

    def "should throw userNotFoundException when user not exists"() {

        given:
        def testUser = TestUserProvider.createUserEntity()
        userRepository.findByPhoneNumber(testUser.getPhoneNumber()) >> Optional.empty()
        def opksCreateDTO = TestOpkProvider.createOpksCreateDTO(testUser.getPhoneNumber())

        when:
        opkService.createOPK(opksCreateDTO)

        then:
        thrown(UserNotFoundException)
    }

    def "should return list of existent opk id's"() {

        given:
        def testUser = TestUserProvider.createUserEntity()
        def opkList = List.of(
                TestOpkProvider.createOpkForUser(testUser),
                TestOpkProvider.createOpkForUser(testUser),
                TestOpkProvider.createOpkForUser(testUser)
        )

        testUser.setOPKS(opkList)

        userRepository.findByPhoneNumber(testUser.getPhoneNumber()) >> Optional.of(testUser)

        when:
        def returnedList = opkService.getOPKsIds(testUser.phoneNumber)

        then:
        def expectedList = opkList.stream().map(opk -> opk.getKeyId()).toList()
        returnedList == expectedList
    }

    def "should throw userNotFoundException when user not exists"() {

        given:
        def testUser = TestUserProvider.createUserEntity()

        userRepository.findByPhoneNumber(testUser.getPhoneNumber()) >> Optional.empty()

        when:
        opkService.getOPKsIds(testUser.phoneNumber)

        then:
        thrown(UserNotFoundException)
    }
}
