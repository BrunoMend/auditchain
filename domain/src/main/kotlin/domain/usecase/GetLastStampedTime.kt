package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.Source
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetLastStampedTime @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository
) : SingleUseCase<Long, GetLastStampedTime.Request>() {

    override fun getRawSingle(request: Request): Single<Long> =
        attestationDataRepository.getLastStampedTime(request.source)

    data class Request(val source: Source)
}