package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SaveAttestation @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository
) : CompletableUseCase<SaveAttestation.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        attestationDataRepository.saveAttestation(request.attestation)

    data class Request(val attestation: Attestation)
}