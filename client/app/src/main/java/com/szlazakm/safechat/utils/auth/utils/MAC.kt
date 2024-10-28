package com.szlazakm.safechat.utils.auth.utils

import android.util.Log
import com.szlazakm.safechat.utils.auth.ecc.MessageKeys
import javax.crypto.Mac

class MAC {

    companion object {

        fun createMac(
            messageKeys: MessageKeys,
            senderPublicKey: ByteArray,
            receiverPublicKey: ByteArray,
            encryptedMessage: ByteArray,
        ): ByteArray {

            Log.d("SafeChat:MAC", "Sender PubKey: ${senderPublicKey.toHex()}")
            Log.d("SafeChat:MAC", "Receiver PubKey: ${receiverPublicKey.toHex()}")
            Log.d("SafeChat:MAC", "MessageKeys cipherKey: ${messageKeys.getCipherKey().encoded.toHex()}")
            Log.d("SafeChat:MAC", "MessageKeys macKey: ${messageKeys.getMacKey().encoded.toHex()}")
            Log.d("SafeChat:MAC", "MessageKeys iv: ${messageKeys.getIv().iv.toHex()}")
            Log.d("SafeChat:MAC", "MessageKeys index: ${messageKeys.getIndex()}")

            val mac: Mac = Mac.getInstance("HmacSHA256")
            mac.init(messageKeys.getMacKey())

            mac.update(senderPublicKey)
            mac.update(receiverPublicKey)

            return mac.doFinal(encryptedMessage).copyOfRange(0, MAC_SIZE)
        }

        fun verifyMac(
            messageKeys: MessageKeys,
            senderPublicKey: ByteArray,
            receiverPublicKey: ByteArray,
            encryptedMessage: ByteArray,
            theirMac: ByteArray
        ): Boolean {

            val ourMac = this.createMac(
                    messageKeys,
                    senderPublicKey,
                    receiverPublicKey,
                    encryptedMessage
                )

            Log.d("SafeChat:MAC", "Our mac: ${ourMac.toHex()}")
            Log.d("SafeChat:MAC", "Their mac: ${theirMac.toHex()}")

            return ourMac.contentEquals(theirMac)
        }

        fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

}