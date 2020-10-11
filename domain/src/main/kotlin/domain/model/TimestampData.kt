package domain.model

import domain.cryptography.SigningKey
import domain.cryptography.VerifyKey
import domain.exception.InvalidDataSignatureException
import domain.utility.toByteArray
import java.io.Serializable
import java.lang.RuntimeException

data class TimestampData(
    val timeInterval: TimeInterval,
    val data: ByteArray?
) : Serializable {
    fun sign(signingKey: SigningKey): ByteArray =
        signingKey.sign(this.toByteArray())

    fun verifySignature(verifyKey: VerifyKey, dataSignature: ByteArray) {
        try {
            if (!verifyKey.verify(this.toByteArray(), dataSignature))
                throw InvalidDataSignatureException()
        } catch (error: RuntimeException) {
            throw InvalidDataSignatureException()
        }
    }
}