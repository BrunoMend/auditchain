package domain.model

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val dateTimestamp: Long,
    val dataSignature: ByteArray,
    var otsData: ByteArray,
    var dateOtsComplete: Long? = null,
    val id: Long? = null
) {
    override fun toString(): String =
        "Interval: ${timeInterval}\n" +
                "Source: ${source}\n"

    val latencyMillis: Long?
        get() = dateOtsComplete?.let { it - dateTimestamp }
}