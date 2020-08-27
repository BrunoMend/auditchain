package domain.model

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val dateTimestamp: Long,
    val dataSignature: ByteArray,
    var otsData: ByteArray,
    var isOtsComplete: Boolean = false,
    val sourceParams: Map<SourceParam, String>? = null,
    val id: Long? = null
)