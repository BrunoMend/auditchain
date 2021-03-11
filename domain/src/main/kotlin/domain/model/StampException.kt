package domain.model

data class StampException(
    val timeInterval: TimeInterval,
    val source: Source,
    val exception: String,
    val dateException: Long = System.currentTimeMillis(),
    val id: Long? = null
)