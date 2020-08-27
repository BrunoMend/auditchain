package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.Attestation
import domain.model.Source
import domain.model.SourceParam
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAttestation @Inject constructor(
    private val attestationDataRepository: AttestationDataRepository
) : SingleUseCase<Attestation, GetAttestation.Request>() {

    override fun getRawSingle(request: Request): Single<Attestation> =
        attestationDataRepository.getAttestation(request.timeInterval, request.source, request.sourceParams)

    data class Request(val source: Source, val timeInterval: TimeInterval, val sourceParams: Map<SourceParam, String>)
}