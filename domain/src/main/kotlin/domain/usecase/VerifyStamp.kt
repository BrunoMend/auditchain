package domain.usecase

import domain.datarepository.BlockchainDataRepository
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

class VerifyStamp(
    private val blockchainDataRepository: BlockchainDataRepository,
    private val executorScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler
) {
    fun getSingle(data: ByteArray, proofFileName: String): Single<List<Map<String, String>>> =
        blockchainDataRepository.verifyStamp(data, proofFileName)
            .subscribeOn(executorScheduler)
            .observeOn(postExecutionScheduler)
}