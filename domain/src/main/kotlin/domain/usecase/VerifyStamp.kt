package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

class VerifyStamp(
    private val timestampDataRepository: TimestampDataRepository,
    private val attestationConfiguration: AttestationConfiguration,
    private val executorScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val logger: Logger
) {

    fun getSingle(data: ByteArray, proofFileName: String): Single<List<Attestation>> =
        timestampDataRepository.verifyStamp(data, proofFileName)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
            .observeOn(postExecutionScheduler)

}