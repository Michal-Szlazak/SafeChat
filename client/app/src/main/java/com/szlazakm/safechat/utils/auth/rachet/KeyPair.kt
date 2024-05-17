package com.szlazakm.safechat.utils.auth.rachet

data class KeyPair(
    val rootKey: RootKey,
    val chainKey: ChainKey
)