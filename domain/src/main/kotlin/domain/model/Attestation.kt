package domain.model

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val sourceParams: Map<SourceParam, String>?,
    val dateTimestamp: Long,
    val dataSignature: ByteArray,
    var otsData: ByteArray,
    var isOtsComplete: Boolean = false,
    val id: Long? = null
)