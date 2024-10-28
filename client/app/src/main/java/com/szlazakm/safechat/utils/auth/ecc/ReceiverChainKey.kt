package com.szlazakm.safechat.utils.auth.ecc

class ReceiverChainKey(key: ByteArray, index: Int, val publicEphemeralKey: ByteArray): ChainKey(key, index){

    override fun getNextChainKey(): ChainKey {
        val nextKey = getBaseMaterial(CHAIN_KEY_SEED)
        return ReceiverChainKey(nextKey, index + 1, this.publicEphemeralKey)
    }

}