package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.datarepository.TimestampDataRepository
import domain.di.IOScheduler
import domain.model.Attestation
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class UpdateOtsData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository,
    private val attestationDataRepository: AttestationDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(attestation: Attestation): Completable =
        timestampDataRepository.performUpdates(attestation.otsData)
            .flatMapCompletable {
                attestation.isOtsUpdated = it
                attestationDataRepository.updateOtsData(attestation)
            }.doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}