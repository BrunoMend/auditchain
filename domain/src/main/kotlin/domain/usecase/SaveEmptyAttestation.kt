package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.di.IOScheduler
import domain.model.Source
import domain.model.TimeInterval
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class SaveEmptyAttestation @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(timeInterval: TimeInterval, source: Source, hasNoData: Boolean): Completable =
        attestationDataRepository.saveEmptyAttestation(timeInterval, source, hasNoData)
            .doOnError {
                logger.log("Error on ${this::class.qualifiedName}: $it")
            }.subscribeOn(executorScheduler)
}