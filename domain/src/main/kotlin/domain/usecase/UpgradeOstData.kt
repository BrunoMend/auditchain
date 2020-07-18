package domain.usecase

import domain.datarepository.TimestampDataRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpgradeOstData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : SingleUseCase<ByteArray, UpgradeOstData.Request>() {

    override fun getRawSingle(request: Request): Single<ByteArray> =
        timestampDataRepository.upgradeOstData(request.otsData)

    data class Request(val otsData: ByteArray)
}