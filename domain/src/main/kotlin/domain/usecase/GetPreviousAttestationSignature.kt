package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.model.TimeInterval
import domain.utility.getPreviousTimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetPreviousAttestationSignature @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration,
    private val attestationDataRepository: AttestationDataRepository
) : SingleUseCase<ByteArray, GetPreviousAttestationSignature.Request>() {

    override fun getRawSingle(request: Request): Single<ByteArray> =
        Single.just(request.timeInterval).map {
            TimeInterval(
                getPreviousTimeInterval(
                    it.startAt,
                    attestationConfiguration.frequencyMillis,
                    attestationConfiguration.delayMillis
                ),
                getPreviousTimeInterval(
                    it.finishIn,
                    attestationConfiguration.frequencyMillis,
                    attestationConfiguration.delayMillis
                )
            )
        }.flatMap { previousTimeInterval ->
            attestationDataRepository.getAttestation(previousTimeInterval, request.source)
                .map { it.dataSignature }
        }

    data class Request(val source: Source, val timeInterval: TimeInterval)
}