package domain.usecase

import domain.datarepository.ConfigurationDataRepository
import domain.datarepository.FileDataRepository
import domain.datarepository.TimestampDataRepository
import domain.di.IOScheduler
import domain.model.AttestationConfiguration
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

class StampData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository,
    private val configurationDataRepository: ConfigurationDataRepository,
    private val fileDataRepository: FileDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(data: ByteArray, proofFileName: String): Completable =
        Single.zip<AttestationConfiguration, ByteArray, Pair<AttestationConfiguration, ByteArray>>(
            configurationDataRepository.getAttestationConfiguration(), timestampDataRepository.stampData(data),
            BiFunction { attestationConfig, proofData -> Pair(attestationConfig, proofData) })
            .flatMapCompletable { fileDataRepository.saveObject(it.first.attestationFilePath, proofFileName, it.second) }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}