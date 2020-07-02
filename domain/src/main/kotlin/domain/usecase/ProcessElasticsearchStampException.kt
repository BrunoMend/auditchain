package domain.usecase

import domain.di.IOScheduler
import domain.model.Attestation
import domain.model.StampException
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ProcessElasticsearchStampException @Inject constructor(
    private val stampElasticsearchData: StampElasticsearchData,
    private val setStampExceptionAsProcessed: SetStampExceptionAsProcessed,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(stampException: StampException): Single<Result<Attestation>> =
        stampElasticsearchData.getSingle(stampException.timeInterval)
            .flatMap {
                setStampExceptionAsProcessed.getCompletable(stampException)
                    .andThen(Single.just(it))
            }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}