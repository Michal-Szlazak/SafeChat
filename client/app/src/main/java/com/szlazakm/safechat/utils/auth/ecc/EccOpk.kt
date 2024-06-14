package com.szlazakm.safechat.utils.auth.ecc

data class EccOpk (
    val privateKey: ByteArray,
    val publicKey: ByteArray,
    val id: Int
)