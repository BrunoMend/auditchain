package data.repository

import data.remote.ElasticsearchRemoteDataSource
import domain.datarepository.ElasticsearchDataRepository
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ElasticsearchRepository @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val elasticsearchRemoteDataSource: ElasticsearchRemoteDataSource
) : ElasticsearchDataRepository {

    override fun getData(timeInterval: TimeInterval): Single<String> =
        configurationRepository.getElasticsearchConfiguration()
            .flatMap {
                elasticsearchRemoteDataSource.getLogs(
                    it.indexPattern,
                    it.getDefaultQuery(timeInterval),
                    it.resultMaxSize
                )
            }.map {
                val initLogs = "\"hits\":["
                val result = it.substring((it.indexOf(initLogs) + initLogs.length - 1), it.lastIndexOf("]") + 1)
                if (result.isNotEmpty() && result != "[]") result else ""
            }

    override fun getFileName(timeInterval: TimeInterval): Single<String> =
        configurationRepository.getElasticsearchConfiguration()
            .map { it.getDefaultFileName(timeInterval) }

}