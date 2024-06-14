package com.szlazakm.safechat.utils.auth

import org.whispersystems.libsignal.kdf.HKDF
import org.whispersystems.libsignal.kdf.HKDFv3
import org.whispersystems.libsignal.ratchet.ChainKey
import org.whispersystems.libsignal.ratchet.RootKey
import org.whispersystems.libsignal.util.ByteUtil


class KDF {

    companion object {
        fun calculateDerivedKeys(masterSecret: ByteArray): DerivedKeys {
            val kdf: HKDF = HKDFv3()
            val derivedSecretBytes: ByteArray =
                kdf.deriveSecrets(masterSecret, "WhisperText".toByteArray(), 64)
            val derivedSecrets: Array<ByteArray> = ByteUtil.split(derivedSecretBytes, 32, 32)
            return DerivedKeys(
                RootKey(kdf, derivedSecrets[0]),
                ChainKey(kdf, derivedSecrets[1], 0)
            )
        }
    }

    class DerivedKeys (val rootKey: RootKey, chainKey: ChainKey) {
        private val chainKey: ChainKey

        init {
            this.chainKey = chainKey
        }

        fun getChainKey(): ChainKey {
            return chainKey
        }
    }
}
