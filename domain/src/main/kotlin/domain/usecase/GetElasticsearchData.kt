package domain.usecase

import domain.datarepository.ElasticsearchDataRepository
import domain.di.IOScheduler
import domain.model.TimeInterval
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

class GetElasticsearchData @Inject constructor(
    private val elasticsearchDataRepository: ElasticsearchDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(timeInterval: TimeInterval): Single<Pair<String, String>> =
        Single.zip<String, String, Pair<String, String>>(
            elasticsearchDataRepository.getData(timeInterval),
            elasticsearchDataRepository.getFileName(timeInterval),
            BiFunction { elasticData, fileName -> Pair(elasticData, fileName) }
        ).doOnError {
            logger.log("Error on ${this::class.qualifiedName}: $it")
        }.subscribeOn(executorScheduler)
}