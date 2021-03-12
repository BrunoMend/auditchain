package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.utility.getPreviousTimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetLastStampedTimeOrDefault @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration,
    private val attestationDataRepository: AttestationDataRepository
) : SingleUseCase<Long, GetLastStampedTimeOrDefault.Request>() {

    override fun getRawSingle(request: Request): Single<Long> =
        attestationDataRepository.getLastAttestation(request.source).map { it.timeInterval.finishIn }
            .onErrorReturn {
                getPreviousTimeInterval(
                    System.currentTimeMillis() - attestationConfiguration.frequencyMillis,
                    attestationConfiguration.frequencyMillis,
                    attestationConfiguration.delayMillis
                )
            }
    data class Request(val source: Source)
}