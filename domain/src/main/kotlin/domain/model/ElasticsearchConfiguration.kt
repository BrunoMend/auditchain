package domain.model

import domain.utility.toDateFormat
import domain.utility.toFileName

data class ElasticsearchConfiguration(
    val elasticHost: String,
    val elasticUser: String,
    val elasticPwds: String,
    val indexPattern: String,
    val rangeParameter: String,
    val resultMaxSize: Int
) {
    fun getDefaultQuery(timeInterval: TimeInterval): String =
        "${rangeParameter}:[${timeInterval.startAt.toDateFormat()} TO ${timeInterval.finishIn.toDateFormat()}]"

    fun getDefaultFileName(timeInterval: TimeInterval): String =
        "${indexPattern}(${rangeParameter}[${timeInterval.startAt}TO${timeInterval.finishIn}])".toFileName()
}