package com.szlazakm.ChatServer.services

import com.szlazakm.ChatServer.helpers.TestSpkProvider
import com.szlazakm.ChatServer.helpers.TestUserProvider
import com.szlazakm.chatserver.utils.SignatureVerifier
import com.szlazakm.chatserver.dtos.SPKCreateOrUpdateDTO
import com.szlazakm.chatserver.entities.SPK
import com.szlazakm.chatserver.exceptionHandling.exceptions.SignatureVerifierException
import com.szlazakm.chatserver.exceptionHandling.exceptions.UserNotFoundException
import com.szlazakm.chatserver.repositories.SPKRepository
import com.szlazakm.chatserver.repositories.UserRepository
import com.szlazakm.chatserver.services.SPKService
import spock.lang.Specification

import java.security.InvalidKeyException
import java.security.spec.InvalidKeySpecException

class SpkServiceSpec extends Specification {

    def spkRepository = Mock(SPKRepository)
    def userRepository = Mock(UserRepository)
    def signatureVerifier = Mock(SignatureVerifier)
    def spkService = new SPKService(spkRepository, userRepository, signatureVerifier)

    def "should create SPK if not exist and user exist"() {

        given:
        def spkCreateDTO = TestSpkProvider.createSpkCreateOrUpdateDTO()

        def testUser = TestUserProvider.createUserEntity()
        userRepository.findByPhoneNumber(spkCreateDTO.getPhoneNumber()) >> Optional.of(testUser)
        spkRepository.findByUserPhoneNumber(spkCreateDTO.getPhoneNumber()) >> Optional.empty()
        signatureVerifier.verifySignature(_ as String, _ as String, _ as String) >> true

        def expectedSpk = SPK.builder()
                .databaseId(null)
                .keyId(spkCreateDTO.getId())
                .signedPreKey(spkCreateDTO.getSignedPreKey())
                .signature(spkCreateDTO.getSignature())
                .timestamp(spkCreateDTO.getTimestamp())
                .user(testUser)
                .build()

        when:
        spkService.createOrUpdateSPK(spkCreateDTO)

        then:
        1 * spkRepository.save(expectedSpk)
    }

    def "should update SPK if not exist and user exist"() {

        given:
        def testUser = TestUserProvider.createUserEntity()
        def existingSpk = TestSpkProvider.createSPK(testUser)
        def updatedId = 2
        def updatedPreKey = "updatedPreKey"
        def updatedSignature = "updatedSignature"
        def updatedTimestamp = 2L

        def spkUpdateDTO = SPKCreateOrUpdateDTO.builder()
            .id(updatedId)
            .signedPreKey(updatedPreKey)
            .signature(updatedSignature)
            .timestamp(updatedTimestamp)
            .build()

        userRepository.findByPhoneNumber(spkUpdateDTO.getPhoneNumber()) >> Optional.of(testUser)
        spkRepository.findByUserPhoneNumber(spkUpdateDTO.getPhoneNumber()) >> Optional.of(existingSpk)
        signatureVerifier.verifySignature(_ as String, _ as String, _ as String) >> true

        def expectedSpk = SPK.builder()
                .databaseId(existingSpk.getDatabaseId())
                .keyId(spkUpdateDTO.getId())
                .signedPreKey(spkUpdateDTO.getSignedPreKey())
                .signature(spkUpdateDTO.getSignature())
                .timestamp(spkUpdateDTO.getTimestamp())
                .user(testUser)
                .build()

        when:
        spkService.createOrUpdateSPK(spkUpdateDTO)

        then:
        1 * spkRepository.save(expectedSpk)
    }

    def "should throw userNotFoundException when user not exists"() {

        given:
        def spkCreateDTO = TestSpkProvider.createSpkCreateOrUpdateDTO()
        userRepository.findByPhoneNumber(spkCreateDTO.getPhoneNumber()) >> Optional.empty()

        when:
        spkService.createOrUpdateSPK(spkCreateDTO)

        then:
        thrown(UserNotFoundException)

    }

    def "should throw signatureVerifierException when signature is incorrect"() {

        given:
        def spkCreateDTO = TestSpkProvider.createSpkCreateOrUpdateDTO()

        def testUser = TestUserProvider.createUserEntity()
        userRepository.findByPhoneNumber(spkCreateDTO.getPhoneNumber()) >> Optional.of(testUser)
        spkRepository.findByUserPhoneNumber(spkCreateDTO.getPhoneNumber()) >> Optional.empty()
        signatureVerifier.verifySignature(_ as String, _ as String, _ as String) >> false

        when:
        spkService.createOrUpdateSPK(spkCreateDTO)

        then:
        thrown(SignatureVerifierException)
    }

    def "should throw signatureVerifierException when signatureVerifier throws exception"() {

        given:
        def spkCreateDTO = TestSpkProvider.createSpkCreateOrUpdateDTO()

        def testUser = TestUserProvider.createUserEntity()
        userRepository.findByPhoneNumber(spkCreateDTO.getPhoneNumber()) >> Optional.of(testUser)
        spkRepository.findByUserPhoneNumber(spkCreateDTO.getPhoneNumber()) >> Optional.empty()
        signatureVerifier.verifySignature(_ as String, _ as String, _ as String) >> {throw exception}

        when:
        spkService.createOrUpdateSPK(spkCreateDTO)

        then:
        thrown(SignatureVerifierException)

        where:
        exception                     | _
        new InvalidKeySpecException() | _
        new InvalidKeyException()     | _
    }
}
