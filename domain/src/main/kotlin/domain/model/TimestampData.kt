package domain.model

import domain.utility.toByteArray
import org.abstractj.kalium.keys.PrivateKey
import org.abstractj.kalium.keys.PublicKey
import org.abstractj.kalium.keys.SigningKey
import org.abstractj.kalium.keys.VerifyKey
import java.io.Serializable

data class TimestampData(
    val timeInterval: TimeInterval,
    val data: ByteArray?
) : Serializable {
    fun sing(privateKey: PrivateKey): ByteArray =
        SigningKey(privateKey.toBytes()).sign(this.toByteArray())

    fun verifySignature(publicKey: PublicKey, signedData: ByteArray): Boolean =
        VerifyKey(publicKey.toBytes()).verify(this.toByteArray(), signedData)
}