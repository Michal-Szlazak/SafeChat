package com.szlazakm.safechat.utils.auth.ecc

import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MessageKeys (
    private val cipherKey: SecretKeySpec,
    private val macKey: SecretKeySpec,
    private val iv: IvParameterSpec,
    private val index: Int
) {

    fun getCipherKey(): SecretKeySpec {
        return cipherKey
    }

    fun getMacKey(): SecretKeySpec {
        return macKey
    }

    fun getIv(): IvParameterSpec {
        return iv
    }

    fun getIndex(): Int {
        return index
    }

    override fun toString(): String {
        return """
            {
                cipherKey: ${cipherKey.encoded.toHexString()},
                macKey: ${macKey.encoded.toHexString()},
                index: $index,
                iv: ${iv.iv.toHexString()}
            }
        """.trimIndent()
    }

    fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}