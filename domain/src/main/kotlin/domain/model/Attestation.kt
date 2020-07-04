package domain.model

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val dateTimestamp: Long,
    var otsData: ByteArray,
    var isOtsUpdated: Boolean = false,
    val id: Long? = null
)