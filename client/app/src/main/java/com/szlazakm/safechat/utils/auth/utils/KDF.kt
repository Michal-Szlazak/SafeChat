package com.szlazakm.safechat.utils.auth.utils

import at.favre.lib.hkdf.HKDF
import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.RootKey
import org.bouncycastle.crypto.Digest
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.params.HKDFParameters
import java.security.SecureRandom


class KDF {

    companion object {

        private const val SALT_SIZE: Int = 32
        private const val SECRET_SIZE: Int = 64

        fun calculateDerivedKeys(masterSecret: ByteArray, oldRootKey: ByteArray): DerivedKeys {

            val derivedSecrets = deriveSecrets(
                masterSecret,
                oldRootKey,
                SECRET_SIZE
            )

            val rootKey = derivedSecrets.copyOfRange(0, 32)
            val chainKey = derivedSecrets.copyOfRange(32, 64)

            return DerivedKeys(
                RootKey(rootKey),
                ChainKey(chainKey, 0)
            )
        }

        fun deriveSecrets(
            inputKeyMaterial: ByteArray?,
            info: ByteArray?,
            outputLength: Int
        ): ByteArray {

            val digest: Digest = SHA256Digest()
            val hkdf = HKDFBytesGenerator(digest)
//            val salt = ByteArray(HASH_OUTPUT_SIZE) TODO make it not constant
            val salt = ByteArray(SALT_SIZE)
//            val random = SecureRandom()
//            random.nextBytes(salt)

            // extract
            hkdf.init(HKDFParameters(inputKeyMaterial, salt, info))

            // expand
            val output = ByteArray(outputLength)
            hkdf.generateBytes(output, 0, outputLength)

            return output
        }
    }

    class DerivedKeys (val rootKey: RootKey, private val chainKey: ChainKey) {

        fun getChainKey(): ChainKey {
            return chainKey
        }
    }
}
