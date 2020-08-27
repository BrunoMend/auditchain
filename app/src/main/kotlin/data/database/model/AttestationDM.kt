package data.database.model

data class AttestationDM(
    val dateStart: Long,
    val dateEnd: Long,
    val source: SourceDM,
    val dateTimestamp: Long,
    val dataSignature: ByteArray,
    val otsData: ByteArray,
    val isOtsComplete: Boolean = false,
    val sourceParams: Map<String, String>? = null,
    val id: Long? = null
)