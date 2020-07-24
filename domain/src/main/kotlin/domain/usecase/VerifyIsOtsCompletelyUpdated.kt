package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

@Deprecated("done directly in the data source")
class VerifyIsOtsCompletelyUpdated @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : CompletableUseCase<VerifyIsOtsCompletelyUpdated.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        timestampDataRepository.verifyIsOtsCompletelyUpdated(request.attestation)

    data class Request(val attestation: Attestation)
}