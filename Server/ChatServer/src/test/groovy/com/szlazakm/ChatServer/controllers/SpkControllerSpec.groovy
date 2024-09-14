package com.szlazakm.ChatServer.controllers

import com.szlazakm.ChatServer.helpers.TestSpkProvider
import com.szlazakm.chatserver.controllers.SPKController
import com.szlazakm.chatserver.services.SPKService
import spock.lang.Specification

class SpkControllerSpec extends Specification{

    def spkService = Mock(SPKService)
    def spkController = new SPKController(spkService)

    def "should call "() {

        given:
        def createSpkDTO = TestSpkProvider.createSpkCreateOrUpdateDTO()

        when:
        spkController.addSpk(createSpkDTO)

        then:
        1 * spkService.createOrUpdateSPK(createSpkDTO)
    }

}
