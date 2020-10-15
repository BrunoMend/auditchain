package domain.cryptography

import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava

class SigningKey(seed: ByteArray) {
    private val lazySodium = LazySodiumJava(SodiumJava())
    private val secretKey: ByteArray

    init {
        checkLength(seed, CRYPTO_BOX_CURVE25519XSALSA20POLY1305_SECRETKEYBYTES)
        this.secretKey = zeros(CRYPTO_BOX_CURVE25519XSALSA20POLY1305_SECRETKEYBYTES * 2)
        val publicKey = zeros(CRYPTO_BOX_CURVE25519XSALSA20POLY1305_PUBLICKEYBYTES)
        if(!lazySodium.cryptoSignSeedKeypair(publicKey, secretKey, seed))
            throw RuntimeException("Failed to generate a key pair")
    }

    fun sign(data: ByteArray): ByteArray {
        val signature = ByteArray(CRYPTO_SIGN_ED25519_BYTES)
        lazySodium.cryptoSignDetached(signature, data, data.size.toLong(), secretKey)
        return signature
    }
}