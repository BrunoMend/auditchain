package data.database.model

data class StampExceptionDM(
    val dateStart: Long,
    val dateEnd: Long,
    val source: String,
    val exception: String,
    val dateException: Long,
    val processed: Boolean = false
)