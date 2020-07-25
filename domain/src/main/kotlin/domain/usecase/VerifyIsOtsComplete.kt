package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class VerifyIsOtsComplete @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : CompletableUseCase<VerifyIsOtsComplete.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        timestampDataRepository.verifyIsOtsComplete(request.attestation)

    data class Request(val attestation: Attestation)
}