package com.szlazakm.safechat.utils.auth.utils

import at.favre.lib.hkdf.HKDF
import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.RootKey


class KDF {

    companion object {
        fun calculateDerivedKeys(masterSecret: ByteArray): DerivedKeys {

            val kdf = HKDF.fromHmacSha256()
            val salt = ByteArray(32)

            val pseudoRandomKey = kdf.extract(salt, masterSecret)
            val derivedSecretBytes = kdf.expand(
                pseudoRandomKey,
                "derivedSecret".toByteArray(),
                64)

            val rootKey = derivedSecretBytes.copyOfRange(0, 32)
            val chainKey = derivedSecretBytes.copyOfRange(32, 64)

            return DerivedKeys(
                RootKey(kdf, rootKey),
                ChainKey(kdf, chainKey, 0)
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
