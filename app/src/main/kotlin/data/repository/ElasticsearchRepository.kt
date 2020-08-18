package data.repository

import data.remote.ElasticsearchRemoteDataSource
import domain.datarepository.ElasticsearchDataRepository
import domain.exception.NoDataToStampException
import domain.exception.NoInternetException
import domain.exception.ServerSideException
import domain.model.ElasticsearchConfiguration
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.net.UnknownHostException
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
        ).onErrorResumeNext {
            when (it) {
                //TODO put this treatment in a retrofit handler
                is HttpException -> Single.error(ServerSideException())
                is UnknownHostException -> Single.error(NoInternetException())
                else -> Single.error(it)
            }
        }.map {
            val initLogs = "\"hits\":["
            val result = it.substring((it.indexOf(initLogs) + initLogs.length - 1), it.lastIndexOf("]") + 1)
            if (result.isNotEmpty() && result != "[]") result.toByteArray() else throw NoDataToStampException(timeInterval, Source.ELASTICSEARCH)
        }
}