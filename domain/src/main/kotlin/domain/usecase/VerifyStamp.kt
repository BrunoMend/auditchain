package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.BlockchainPublication
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyStamp @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : SingleUseCase<List<BlockchainPublication>, VerifyStamp.Request>() {

    override fun getRawSingle(request: Request): Single<List<BlockchainPublication>> =
        timestampDataRepository.verifyStamp(request.originalData, request.otsData)

    data class Request(val originalData: ByteArray, val otsData: ByteArray)
}