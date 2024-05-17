package com.szlazakm.safechat.utils.auth

import com.szlazakm.safechat.utils.auth.rachet.ChainKey
import com.szlazakm.safechat.utils.auth.rachet.KeyPair
import com.szlazakm.safechat.utils.auth.rachet.RootKey
import org.whispersystems.libsignal.kdf.HKDF
import org.whispersystems.libsignal.kdf.HKDFv3


class KDF {

    companion object {

        val HKDFv3: HKDFv3 = HKDFv3()
        fun calculateDerivedKeys(masterSecret: ByteArray): KeyPair {
            val kdf: HKDF = HKDFv3
            val derivedSecretBytes: ByteArray =
                kdf.deriveSecrets(masterSecret, "WhisperText".toByteArray(), 64)

            val rootKey = RootKey(derivedSecretBytes.copyOfRange(0, 32))
            val chainKey = ChainKey(derivedSecretBytes.copyOfRange(32, 64), 0)

            return KeyPair(rootKey, chainKey)
        }
    }
}
