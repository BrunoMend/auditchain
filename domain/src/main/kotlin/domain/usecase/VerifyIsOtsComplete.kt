package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyIsOtsComplete @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : SingleUseCase<Attestation, VerifyIsOtsComplete.Request>() {

    override fun getRawSingle(request: Request): Single<Attestation> =
        timestampDataRepository.checkIsOtsComplete(request.attestation)

    data class Request(val attestation: Attestation)
}