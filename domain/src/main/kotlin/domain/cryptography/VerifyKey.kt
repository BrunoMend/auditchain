package domain.cryptography

import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava


class VerifyKey(private val key: ByteArray) {
    private val lazySodium = LazySodiumJava(SodiumJava())

    init {
        checkLength(key, CRYPTO_BOX_CURVE25519XSALSA20POLY1305_PUBLICKEYBYTES)
    }

    fun verify(data: ByteArray, dataSignature: ByteArray): Boolean {
        checkLength(dataSignature, CRYPTO_SIGN_ED25519_BYTES)
        return lazySodium.cryptoSignVerifyDetached(dataSignature, data, data.size, key)
    }
}