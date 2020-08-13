package domain.model

import java.io.Serializable

data class TimestampData(
    val timeInterval: TimeInterval,
    val data: ByteArray,
    val previousAttestationSignature: ByteArray
) : Serializable