package domain.usecase

import domain.di.IOScheduler
import domain.model.Attestation
import domain.model.Source
import domain.utility.Logger
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class ProcessAllElasticsearchStampExceptions @Inject constructor(
    private val getUnprocessedStampExceptions: GetUnprocessedStampExceptions,
    private val processElasticsearchStampException: ProcessElasticsearchStampException,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getObservable(): Observable<Result<Attestation>> =
        getUnprocessedStampExceptions.getSingle(Source.ELASTICSEARCH)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { processElasticsearchStampException.getSingle(it) }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}