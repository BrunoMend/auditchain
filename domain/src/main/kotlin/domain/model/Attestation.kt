package domain.model

import domain.utility.toKeyValueString

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val sourceParams: Map<SourceParam, String>?,
    val dateTimestamp: Long,
    val dataSignature: ByteArray,
    var otsData: ByteArray,
    var dateOtsComplete: Long? = null,
    val id: Long? = null
) {
    override fun toString(): String =
        "Interval: ${timeInterval}\n" +
                "Source: ${source}\n" +
                "${sourceParams.toKeyValueString()}\n"

    val latencyMillis: Long?
        get() = dateOtsComplete?.let { dateTimestamp - it }
}