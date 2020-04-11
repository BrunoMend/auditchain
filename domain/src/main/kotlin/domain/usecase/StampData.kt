package domain.usecase

import domain.datarepository.BlockchainDataRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler

class StampData(
    private val blockchainDataRepository: BlockchainDataRepository,
    private val executorScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler
) {
    fun getCompletable(data: ByteArray, proofFileName: String): Completable {
        return blockchainDataRepository.stampData(data, proofFileName)
            .subscribeOn(executorScheduler)
            .observeOn(postExecutionScheduler)
    }
}