package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.di.IOScheduler
import domain.model.Attestation
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyStamp @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(data: ByteArray, proofFileName: String): Single<List<Attestation>> =
        timestampDataRepository.verifyStamp(data, proofFileName)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}