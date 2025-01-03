package com.szlazakm.safechat.utils.auth.bob

import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair

data class InitializationKeyBundle(

    val aliceIdentityKey: ByteArray,
    val aliceEphemeralKey: ByteArray,
    val bobOpk: ByteArray? = null,
    val bobSpk: ByteArray,
    val bobPrivateIdentityKey: ByteArray,
    val bobEphemeralRatchetEccKeyPair: EccKeyPair,
)
