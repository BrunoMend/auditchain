package data.repository

import data.remote.ElasticsearchRemoteDataSource
import domain.datarepository.ElasticsearchDataRepository
import domain.exception.NoDataException
import domain.model.ElasticsearchConfiguration
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ElasticsearchRepository @Inject constructor(
    private val elasticsearchConfiguration: ElasticsearchConfiguration,
    private val elasticsearchRemoteDataSource: ElasticsearchRemoteDataSource
) : ElasticsearchDataRepository {

    override fun getElasticsearchData(timeInterval: TimeInterval): Single<ByteArray> =
        elasticsearchRemoteDataSource.getLogs(
            elasticsearchConfiguration.indexPattern,
            elasticsearchConfiguration.getDefaultQuery(timeInterval),
            elasticsearchConfiguration.resultMaxSize
        ).map {
            val initLogs = "\"hits\":["
            val result = it.substring((it.indexOf(initLogs) + initLogs.length - 1), it.lastIndexOf("]") + 1)
            if (result.isNotEmpty() && result != "[]") result.toByteArray() else throw NoDataException(timeInterval)
        }
}