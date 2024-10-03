package com.szlazakm.safechat.utils.auth.ecc

import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec



class MessageKeys (
    private val cipherKey: SecretKeySpec?,
    private val macKey: SecretKeySpec?,
    private val iv: IvParameterSpec?,
    private val counter: Int
) {

    fun getCipherKey(): SecretKeySpec? {
        return cipherKey
    }

    fun getMacKey(): SecretKeySpec? {
        return macKey
    }

    fun getIv(): IvParameterSpec? {
        return iv
    }

    fun getCounter(): Int {
        return counter
    }
}