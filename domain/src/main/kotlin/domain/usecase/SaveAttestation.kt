package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.di.IOScheduler
import domain.model.Attestation
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

@Deprecated("Used directly in repositories")
class SaveAttestation @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(attestation: Attestation): Completable =
        attestationDataRepository.saveAttestation(attestation)
            .doOnError {
                logger.log("Error on ${this::class.qualifiedName}: $it")
            }.subscribeOn(executorScheduler)
}