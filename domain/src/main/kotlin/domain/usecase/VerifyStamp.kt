package domain.usecase

import domain.datarepository.ConfigurationDataRepository
import domain.datarepository.FileDataRepository
import domain.datarepository.TimestampDataRepository
import domain.di.IOScheduler
import domain.model.Attestation
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyStamp @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository,
    private val configurationDataRepository: ConfigurationDataRepository,
    private val fileDataRepository: FileDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(data: ByteArray, proofFileName: String): Single<List<Attestation>> =
        configurationDataRepository.getAttestationConfiguration()
            .flatMap { fileDataRepository.getObject<ByteArray>(it.attestationFilePath, proofFileName) }
            .flatMap { timestampDataRepository.verifyStamp(data, it) }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}