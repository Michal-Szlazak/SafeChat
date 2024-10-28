package com.szlazakm.safechat.utils.auth.bob

import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.ecc.ReceiverChainKey
import com.szlazakm.safechat.utils.auth.ecc.RootKey

data class InitialMessageDecryptionBundle(
    val aliceEphemeralRatchetEccPublicKey: ByteArray,
    val ratchetKeyPair: Pair<RootKey, ChainKey>,
    val receiverChainKey: ReceiverChainKey,
    val ourIdentityPublicKey: ByteArray,
    val theirIdentityPublicKey: ByteArray,
    val bobEphemeralRatchetEccKeyPair: EccKeyPair
)
