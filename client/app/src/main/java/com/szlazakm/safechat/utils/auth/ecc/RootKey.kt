package com.szlazakm.safechat.utils.auth.ecc

import com.szlazakm.safechat.utils.auth.utils.DiffieHellman
import com.szlazakm.safechat.utils.auth.utils.KDF
import java.security.InvalidKeyException
import java.security.interfaces.ECPublicKey


class RootKey(val key: ByteArray) {

    @Throws(InvalidKeyException::class)
    fun createChain(
        theirRatchetKey: ECPublicKey,   //Signed pre-key
        ourRatchetKey: EccKeyPair       //Our ephemeral key for random
    ): Pair<RootKey, ChainKey> {
        val sharedSecret: ByteArray =
            DiffieHellman.createSharedSecret(ourRatchetKey.privateKey, theirRatchetKey.encoded)
        val derivedSecretBytes: ByteArray = KDF.deriveSecrets(
            sharedSecret,
            key,
            "WhisperRatchet".toByteArray(),
            64
        )

        val rootKeyBytes = derivedSecretBytes.copyOfRange(0, 32)
        val chainKeyBytes = derivedSecretBytes.copyOfRange(32, 64)

        val newRootKey = RootKey(rootKeyBytes)
        val newChainKey = ChainKey(chainKeyBytes, 0)

        return Pair(newRootKey, newChainKey)
    }

}