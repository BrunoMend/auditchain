package domain.usecase

import domain.di.IOScheduler
import domain.model.BlockchainPublication
import domain.model.Source
import domain.model.TimeInterval
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyElasticsearchData @Inject constructor(
    private val getAttestation: GetAttestation,
    private val getElasticsearchData: GetElasticsearchData,
    private val verifyStamp: VerifyStamp,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(timeInterval: TimeInterval): Single<Result<List<BlockchainPublication>>> =
        Single.just(timeInterval)
            .flatMap {
                getAttestation.getSingle(Source.ELASTICSEARCH, timeInterval)
                    .flatMap { attestation ->
                        getElasticsearchData.getSingle(timeInterval)
                            .flatMap { originalData ->
                                verifyStamp.getSingle(originalData, attestation.otsData)
                                    .map { Result.success(it) }
                            }
                    }
            }
            .onErrorResumeNext { Single.just(Result.failure(it)) }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}