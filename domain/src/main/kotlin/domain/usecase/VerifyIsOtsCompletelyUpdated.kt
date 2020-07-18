package domain.usecase

import domain.datarepository.TimestampDataRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyIsOtsCompletelyUpdated @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : SingleUseCase<Boolean, VerifyIsOtsCompletelyUpdated.Request>() {

    override fun getRawSingle(request: Request): Single<Boolean> =
        timestampDataRepository.verifyIsOtsCompletelyUpdated(request.otsData)

    data class Request(val otsData: ByteArray)
}