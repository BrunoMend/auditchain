package domain.model

import domain.exception.InvalidDataSignatureException
import domain.utility.toByteArray
import org.abstractj.kalium.keys.SigningKey
import org.abstractj.kalium.keys.VerifyKey
import java.io.Serializable
import java.lang.RuntimeException

data class TimestampData(
    val timeInterval: TimeInterval,
    val data: ByteArray?
) : Serializable {
    fun sign(signingKey: SigningKey): ByteArray =
        signingKey.sign(this.toByteArray())

    fun verifySignature(verifyKey: VerifyKey, dataSignature: ByteArray): Boolean =
        try {
            verifyKey.verify(this.toByteArray(), dataSignature)
        } catch (error: RuntimeException) {
            throw InvalidDataSignatureException()
        }
}