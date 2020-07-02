package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.di.IOScheduler
import domain.model.BlockchainPublication
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyStamp @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(originalData: ByteArray, otsData: ByteArray): Single<List<BlockchainPublication>> =
        timestampDataRepository.verifyStamp(originalData, otsData)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}