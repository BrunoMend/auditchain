package domain.model

data class TimestampResult(
    val dataSignature: ByteArray,
    val otsData: ByteArray
)