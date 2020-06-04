package data.database.model

data class AttestationDM(
    val id: Int,
    val dateStart: Long,
    val dateEnd: Long,
    val source: String,
    val otsData: ByteArray,
    val dateTimestamp: Long
)