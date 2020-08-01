package domain.model

data class StampException (
    val timeInterval: TimeInterval,
    val source: Source,
    val exception: String,
    val dateException: Long = System.currentTimeMillis(),
    val processed: Boolean = false,
    val id: Long? = null
)