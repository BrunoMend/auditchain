package data.repository

import data.remote.ElasticsearchRemoteDataSource
import domain.datarepository.ElasticsearchDataRepository
import domain.exception.NoDataToStampException
import domain.model.*
import domain.utility.toDateFormat
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ElasticsearchRepository @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration,
    private val elasticsearchRemoteDataSource: ElasticsearchRemoteDataSource
) : ElasticsearchDataRepository {

    override fun getElasticsearchData(indexPattern: String, timeInterval: TimeInterval): Single<ByteArray> =
         elasticsearchRemoteDataSource.getLogs(
                    indexPattern,
                    timeInterval.toElasticsearchQuery()
                ).map { handleResultData(it, timeInterval, indexPattern) }

    private fun handleResultData(data: String, timeInterval: TimeInterval, indexPattern: String): ByteArray {
        val initLogs = "\"hits\":["
        val result = data.substring((data.indexOf(initLogs) + initLogs.length - 1), data.lastIndexOf("]") + 1)
        return if (result.isNotEmpty() && result != "[]")
            result.toByteArray()
        else {
            if (System.currentTimeMillis() - timeInterval.finishIn > attestationConfiguration.tryAgainTimeoutMillis)
                byteArrayOf(0)
            else throw NoDataToStampException(timeInterval, Source.ELASTICSEARCH, mapOf(Pair(SourceParam.INDEX_PATTERN, indexPattern)))
        }
    }

    private fun TimeInterval.toElasticsearchQuery() =
        "@timestamp:[${startAt.toDateFormat()} TO ${finishIn.toDateFormat()}]"
}