package domain.usecase

import domain.datarepository.ElasticSearchDataRepository
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

class GetElasticsearchData(
    private val elasticSearchDataRepository: ElasticSearchDataRepository,
    private val executorScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler
) {
    fun getSingle(query: String): Single<ByteArray> =
        elasticSearchDataRepository.getData(query)
            .subscribeOn(executorScheduler)
            .observeOn(postExecutionScheduler)
}