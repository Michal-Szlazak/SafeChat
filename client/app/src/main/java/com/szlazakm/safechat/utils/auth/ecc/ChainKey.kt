package com.szlazakm.safechat.utils.auth.ecc

import com.szlazakm.safechat.utils.auth.utils.KDF
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class ChainKey(val key: ByteArray, val index: Int) {

    val MESSAGE_KEY_SEED: ByteArray = byteArrayOf(0x01)
    val CHAIN_KEY_SEED: ByteArray = byteArrayOf(0x02)

    fun getNextChainKey(): ChainKey {
        val nextKey = getBaseMaterial(CHAIN_KEY_SEED)
        return ChainKey(nextKey, index + 1)
    }

    fun getMessageKeys(): MessageKeys {
        val inputKeyMaterial = getBaseMaterial(MESSAGE_KEY_SEED)
        val keyMaterialBytes: ByteArray = KDF.deriveSecrets(
            inputKeyMaterial,
            "MessageKeys".toByteArray(),
            80
        )

        val cipherKeyBytes = keyMaterialBytes.copyOfRange(0, 32)
        val macKeyBytes = keyMaterialBytes.copyOfRange(32, 64)
        val ivBytes = keyMaterialBytes.copyOfRange(64, 80)

        val cipherKey = SecretKeySpec(cipherKeyBytes, "AES")
        val macKey = SecretKeySpec(macKeyBytes, "HmacSHA256")
        val iv = IvParameterSpec(ivBytes)

        return MessageKeys(
            cipherKey,
            macKey,
            iv,
            index
        )
    }

    private fun getBaseMaterial(seed: ByteArray): ByteArray {
        try {
            val mac: Mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(key, "HmacSHA256"))

            return mac.doFinal(seed)
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: InvalidKeyException) {
            throw AssertionError(e)
        }
    }
}