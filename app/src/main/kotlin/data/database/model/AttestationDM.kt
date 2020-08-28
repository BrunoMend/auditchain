package data.database.model

data class AttestationDM(
    val dateStart: Long,
    val dateEnd: Long,
    val source: SourceDM,
    val sourceParams: Map<String, String>?,
    val dateTimestamp: Long,
    val dataSignature: ByteArray,
    val otsData: ByteArray,
    val isOtsComplete: Boolean = false,
    val id: Long? = null
)