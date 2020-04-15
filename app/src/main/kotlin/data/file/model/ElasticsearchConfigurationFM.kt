package data.file.model

data class ElasticsearchConfigurationFM(
    val elasticHost: String,
    val elasticUser: String,
    val elasticPwds: String,
    val indexPattern: String,
    val rangeParameter: String,
    val resultMaxSize: Int
)