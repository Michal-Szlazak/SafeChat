package com.szlazakm.safechat.utils.auth.helpers

import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.ecc.ReceiverChainKey
import com.szlazakm.safechat.utils.auth.ecc.RootKey

class KeyBundle (
    val identityKeyPair: EccKeyPair,
    val signedKeyPair: EccKeyPair,
    val opkKeyPair: EccKeyPair,
    val ephemeralKeyPair: EccKeyPair,
    val ratchetEccKeyPair: EccKeyPair,
    var senderRatchetChain: Pair<RootKey, ChainKey>?,
    var receiverRatchetChain: Pair<RootKey, ReceiverChainKey>?
)