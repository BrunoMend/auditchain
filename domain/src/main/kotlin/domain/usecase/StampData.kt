package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler

class StampData(
    private val timestampDataRepository: TimestampDataRepository,
    private val executorScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val logger: Logger
) {

    fun getCompletable(data: ByteArray, proofFileName: String): Completable =
        timestampDataRepository.stampData(data, proofFileName)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
            .observeOn(postExecutionScheduler)

}