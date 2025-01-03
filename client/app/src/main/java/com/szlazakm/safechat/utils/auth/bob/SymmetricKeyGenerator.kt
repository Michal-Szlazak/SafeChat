package com.szlazakm.safechat.utils.auth.bob

import com.szlazakm.safechat.utils.auth.utils.DiffieHellman

class SymmetricKeyGenerator {

    companion object {

        fun generateSymmetricKey(initializationKeyBundle: InitializationKeyBundle) : ByteArray {

            val aliceIdentityKey = initializationKeyBundle.aliceIdentityKey
            val aliceEphemeralKey = initializationKeyBundle.aliceEphemeralKey
            val bobOpk = initializationKeyBundle.bobOpk
            val bobSpk = initializationKeyBundle.bobSpk
            val bobPrivateIdentityKey = initializationKeyBundle.bobPrivateIdentityKey

            val dh1 = DiffieHellman.createSharedSecret(bobSpk, aliceIdentityKey)
            val dh2 = DiffieHellman.createSharedSecret(bobPrivateIdentityKey, aliceEphemeralKey)
            val dh3 = DiffieHellman.createSharedSecret(bobSpk, aliceEphemeralKey)
            val dh4 = if (bobOpk != null) DiffieHellman.createSharedSecret(bobOpk, aliceEphemeralKey) else byteArrayOf()

            return dh1 + dh2 + dh3 + dh4
        }
    }
}