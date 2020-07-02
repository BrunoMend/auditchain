package domain.usecase

import domain.di.IOScheduler
import domain.model.Attestation
import domain.utility.Logger
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class StampElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val stampElasticsearchData: StampElasticsearchData,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getObservable(startAt: Long, finishIn: Long): Observable<Result<Attestation>> =
        getTimeIntervals.getSingle(startAt, finishIn)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { stampElasticsearchData.getSingle(it) }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}