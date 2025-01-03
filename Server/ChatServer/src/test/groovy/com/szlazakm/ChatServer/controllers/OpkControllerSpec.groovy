package com.szlazakm.ChatServer.controllers

import com.szlazakm.ChatServer.helpers.TestOpkProvider
import com.szlazakm.chatserver.controllers.OPKController
import com.szlazakm.chatserver.services.OPKService
import spock.lang.Specification

class OpkControllerSpec extends Specification{

    def opkService = Mock(OPKService)
    def opkController = new OPKController(opkService)

    def "should call createOpk when POST createOpk request sent"() {

        given:
        def phoneNumber = "123123123"
        def createOpksDTO = TestOpkProvider.createOpksCreateDTO(phoneNumber)

        when:
        opkController.createOPKs(createOpksDTO)

        then:
        1 * opkService.createOPK(createOpksDTO)
    }

    def "should return list of id's when GET opk request sent"() {

        given:
        def phoneNumber = "123123123"
        def expectedIds = List.of(1, 2, 3, 4)

        when:
        def returnedIds = opkController.getOPKs(phoneNumber)

        then:
        1 * opkService.getOPKsIds(phoneNumber) >> expectedIds
        expectedIds == returnedIds
    }
}
