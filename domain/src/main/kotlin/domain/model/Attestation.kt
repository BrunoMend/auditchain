package domain.model

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val dateTimestamp: Long,
    val otsData: ByteArray,
    val isOtsUpdated: Boolean = false,
    val id: Long? = null
)