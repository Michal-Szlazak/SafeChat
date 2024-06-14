package com.szlazakm.safechat.utils.auth.ecc

data class EccSignedKeyPair (
    val privateKey: ByteArray,
    val publicKey: ByteArray,
    val signature: ByteArray,
    val id: Int,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EccSignedKeyPair

        if (!privateKey.contentEquals(other.privateKey)) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = privateKey.contentHashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}