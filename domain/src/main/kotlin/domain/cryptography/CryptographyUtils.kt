package domain.cryptography

const val CRYPTO_BOX_CURVE25519XSALSA20POLY1305_SECRETKEYBYTES = 32
const val CRYPTO_BOX_CURVE25519XSALSA20POLY1305_PUBLICKEYBYTES = 32
const val CRYPTO_SIGN_ED25519_BYTES = 64

fun checkLength(data: ByteArray?, size: Int) {
    if (data == null || data.size != size) throw RuntimeException("Invalid size")
}

fun zeros(n: Int): ByteArray {
    return ByteArray(n)
}
