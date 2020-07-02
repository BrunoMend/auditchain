package domain.usecase

import domain.di.IOScheduler
import domain.exception.className
import domain.model.Attestation
import domain.model.Source
import domain.model.StampException
import domain.model.TimeInterval
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class StampElasticsearchData @Inject constructor(
    private val getElasticsearchData: GetElasticsearchData,
    private val stampData: StampData,
    private val saveAttestation: SaveAttestation,
    private val saveStampException: SaveStampException,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(timeInterval: TimeInterval): Single<Result<Attestation>> =
        Single.just(timeInterval)
            .flatMap {
                getElasticsearchData.getSingle(timeInterval)
                    .flatMap { data ->
                        stampData.getSingle(data)
                            .flatMap { otsData ->
                                val attestation =
                                    Attestation(
                                        timeInterval,
                                        Source.ELASTICSEARCH,
                                        System.currentTimeMillis(),
                                        otsData
                                    )
                                saveAttestation.getCompletable(attestation)
                                    .andThen(Single.just(attestation))
                            }
                    }
                    .map { Result.success(it) }
            }
            .onErrorResumeNext { error ->
                saveStampException.getCompletable(
                    StampException(
                        timeInterval,
                        Source.ELASTICSEARCH,
                        error.className
                    )
                ).andThen(Single.just(Result.failure(error)))
            }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}