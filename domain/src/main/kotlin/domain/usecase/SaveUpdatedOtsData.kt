package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SaveUpdatedOtsData @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository
) {
    fun getCompletable(attestation: Attestation): Completable =
        attestationDataRepository.updateOtsData(attestation)
}