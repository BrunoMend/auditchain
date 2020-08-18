package domain.usecase

import domain.datarepository.TimestampDataRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : SingleUseCase<ByteArray, StampData.Request>() {

    override fun getRawSingle(request: Request): Single<ByteArray> =
        timestampDataRepository.stampData(request.data)

    data class Request(val data: ByteArray)
}