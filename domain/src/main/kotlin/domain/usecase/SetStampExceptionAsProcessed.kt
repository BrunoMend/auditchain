package domain.usecase

import domain.datarepository.StampExceptionDataRepository
import domain.di.IOScheduler
import domain.model.StampException
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class SetStampExceptionAsProcessed @Inject constructor(
    private val stampExceptionDataRepository: StampExceptionDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(stampException: StampException): Completable =
        stampExceptionDataRepository.setStampExceptionAsProcessed(stampException)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}