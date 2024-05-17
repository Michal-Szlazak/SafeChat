package com.szlazakm.safechat.utils.auth.rachet

import com.szlazakm.safechat.utils.auth.DiffieHellman
import com.szlazakm.safechat.utils.auth.KDF

class RootKey (
    var key: ByteArray
) {

    fun createChain(theirRatchetKey: ChainKey, ourRatchetKey: ChainKey): KeyPair {

        val sharedSecret = DiffieHellman.createSharedSecret(
            ourRatchetKey.key,
            theirRatchetKey.key
        )

        val kdf = KDF.HKDFv3
        val derivedKeys = kdf.deriveSecrets(sharedSecret, "SafeChatRatchet".toByteArray(), 64)

        val rootKey = RootKey(derivedKeys.copyOfRange(0, 32))
        val chainKey = ChainKey(derivedKeys.copyOfRange(32, 64), 0)

        return KeyPair(rootKey, chainKey)
    }

}