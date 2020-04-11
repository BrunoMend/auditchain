package domain.model

data class Configuration(
    val elasticHost: String,
    val elasticUser: String,
    val elasticPwds: String,
    val frequency: Long,
    val delay: Long,
    val indexPattern: String,
    val rangeParameter: String,
    val resultMaxSize: Int,
    val filePath: String,
    val instants: List<Long>
)