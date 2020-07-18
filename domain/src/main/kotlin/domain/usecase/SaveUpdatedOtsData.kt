package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SaveUpdatedOtsData @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository
) : CompletableUseCase<SaveUpdatedOtsData.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        attestationDataRepository.updateOtsData(request.attestation)

    data class Request(val attestation: Attestation)
}