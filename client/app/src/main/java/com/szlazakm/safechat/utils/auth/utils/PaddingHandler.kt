package com.szlazakm.safechat.utils.auth.utils

class PaddingHandler {

    companion object {

        fun removePadding(paddedMessageBytes: ByteArray): ByteArray {
            var paddingIndex = paddedMessageBytes.size

            for (i in paddedMessageBytes.indices.reversed()) {
                if (paddedMessageBytes[i] != 0.toByte()) {
                    paddingIndex = i + 1
                    break
                }
            }

            return paddedMessageBytes.copyOfRange(0, paddingIndex)
        }

        fun addPadding(unPaddedMessageBytes: ByteArray):ByteArray {

            val blockSize = 16
            val paddingLength = blockSize - (unPaddedMessageBytes.size % blockSize)
            return unPaddedMessageBytes + ByteArray(paddingLength) { 0 }
        }
    }
}