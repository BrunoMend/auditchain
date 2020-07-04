package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.di.IOScheduler
import domain.model.Source
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetLastStampedTime @Inject  constructor(
    private val attestationDataRepository: AttestationDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(source: Source): Single<Long> =
        attestationDataRepository.getLastStampedTime(source)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}