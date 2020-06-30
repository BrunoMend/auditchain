package domain.usecase

import domain.datarepository.ElasticsearchDataRepository
import domain.di.IOScheduler
import domain.model.Attestation
import domain.model.TimeInterval
import domain.utility.Logger
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class StampElasticsearchData @Inject constructor(
    private val elasticsearchDataRepository: ElasticsearchDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getObservable(intervals: List<TimeInterval>): Observable<Attestation> =
        elasticsearchDataRepository
            .stampElasticsearchData(intervals)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}