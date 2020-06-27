package data.database.model

data class AttestationDM(
    val dateStart: Long,
    val dateEnd: Long,
    val source: String,
    val dateTimestamp: Long? = null,
    val otsData: ByteArray? = null,
    var isOtsUpdated: Boolean = false,
    val hasNoData: Boolean = false
)