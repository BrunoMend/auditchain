package domain.model

data class ElasticsearchConfiguration(
    val elasticHost: String,
    val elasticUser: String,
    val elasticPwds: String,
    val indexPatterns: List<String>
)