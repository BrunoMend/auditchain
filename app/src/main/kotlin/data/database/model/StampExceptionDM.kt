package data.database.model

data class StampExceptionDM(
    val dateStart: Long,
    val dateEnd: Long,
    val source: SourceDM,
    val exception: String,
    val dateException: Long,
    val id: Long? = null
)