package com.szlazakm.safechat.utils.auth.alice

import com.szlazakm.safechat.utils.auth.utils.DiffieHellman

class SymmetricKeyGenerator {

    companion object {

        fun generateSymmetricKey(initializationKeyBundle: InitializationKeyBundle) : ByteArray {

            val bobIdentityKey = initializationKeyBundle.bobPublicIdentityKey
            val bobSignedPreKey = initializationKeyBundle.bobPublicSignedPreKey
            val bobPublicOpk = initializationKeyBundle.bobPublicOpk
            val alicePrivateIdentityKey = initializationKeyBundle.alicePrivateIdentityKey
            val aliceEphemeralPrivateKey = initializationKeyBundle.aliceEphemeralKeyPair.privateKey

            val dh1 = DiffieHellman.createSharedSecret(alicePrivateIdentityKey, bobSignedPreKey)
            val dh2 = DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobIdentityKey)
            val dh3 = DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobSignedPreKey)
            val dh4 = if (bobPublicOpk != null) DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobPublicOpk) else byteArrayOf()

            return dh1 + dh2 + dh3 + dh4
        }
    }
}