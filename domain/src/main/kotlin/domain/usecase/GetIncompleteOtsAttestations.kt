package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetIncompleteOtsAttestations @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository
) : SingleUseCase<List<Attestation>, Unit>() {

    override fun getRawSingle(request: Unit): Single<List<Attestation>> =
        attestationDataRepository.getIncompleteOtsAttestations()

}