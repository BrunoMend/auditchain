package data.repository

import data.remote.ElasticsearchRemoteDataSource
import domain.datarepository.ElasticsearchDataRepository
import domain.model.ElasticsearchConfiguration
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single

class ElasticsearchRepository(
    private val elasticsearchConfiguration: ElasticsearchConfiguration,
    private val elasticsearchRemoteDataSource: ElasticsearchRemoteDataSource
) : ElasticsearchDataRepository {

    override fun getData(timeInterval: TimeInterval): Single<String> =
        elasticsearchRemoteDataSource.getLogs(
            elasticsearchConfiguration.indexPattern,
            elasticsearchConfiguration.getDefaultQuery(timeInterval),
            elasticsearchConfiguration.resultMaxSize
        ).map {
            val initLogs = "\"hits\":["
            it.substring((it.indexOf(initLogs) + initLogs.length - 1), it.lastIndexOf("]") + 1)
        }

}