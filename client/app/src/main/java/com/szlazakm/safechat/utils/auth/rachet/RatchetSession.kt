package com.szlazakm.safechat.utils.auth.rachet

import com.szlazakm.safechat.utils.auth.KDF
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper

class RatchetSession(
    var rootKey: RootKey,
    var sendingChainKey: ChainKey,
    var ourPrivateRatchetKey: ChainKey,
    var ourPublicRatchetKey: ChainKey,
    var receivingChainKey: ChainKey,
    var theirRatchetKey: ChainKey,
    var messageNumber: Int,
    var previousChainLength: Int
) {

    fun createNewChain(theirRatchetKey: ChainKey) {

        this.theirRatchetKey = theirRatchetKey
        previousChainLength = messageNumber
        messageNumber = 0

        val newReceivingChain = rootKey.createChain(theirRatchetKey, ourPrivateRatchetKey)
        rootKey.key = newReceivingChain.rootKey.key
        receivingChainKey = newReceivingChain.chainKey

        val newKeyPair = EccKeyHelper.generateKeyPair()

        ourPrivateRatchetKey = ChainKey(newKeyPair.privateKey, 0)
        ourPublicRatchetKey = ChainKey(newKeyPair.publicKey, 0)

        val newSendingChain = rootKey.createChain(theirRatchetKey, ourPrivateRatchetKey)
        rootKey.key = newSendingChain.rootKey.key
        sendingChainKey = newSendingChain.chainKey
    }

    fun rotateSendingChain() {

        val newSendingKey = KDF.HKDFv3.deriveSecrets(
            sendingChainKey.key,
            "WhisperRatchet".toByteArray(),
            32
        )

        sendingChainKey = ChainKey(newSendingKey, sendingChainKey.index + 1)
    }

    fun rotateReceivingChain() {

        val newReceivingKey = KDF.HKDFv3.deriveSecrets(
            receivingChainKey.key,
            "WhisperRatchet".toByteArray(),
            32
        )

        receivingChainKey = ChainKey(newReceivingKey, receivingChainKey.index + 1)
    }
}