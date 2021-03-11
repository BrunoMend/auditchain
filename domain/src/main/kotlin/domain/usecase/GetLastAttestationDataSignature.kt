package domain.usecase

import domain.datarepository.AttestationDataRepository
import domain.exception.InvalidTimeIntervalException
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.model.TimeInterval
import domain.utility.getNextTimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetLastAttestationDataSignature @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration,
    private val attestationDataRepository: AttestationDataRepository
) : SingleUseCase<ByteArray, GetLastAttestationDataSignature.Request>() {

    override fun getRawSingle(request: Request): Single<ByteArray> =
        attestationDataRepository.getLastAttestation(request.source)
            .map { lastAttestation ->

                val expectedTimeInterval = TimeInterval(
                    getNextTimeInterval(
                        lastAttestation.timeInterval.startAt,
                        attestationConfiguration.frequencyMillis,
                        attestationConfiguration.delayMillis
                    ),
                    getNextTimeInterval(
                        lastAttestation.timeInterval.finishIn,
                        attestationConfiguration.frequencyMillis,
                        attestationConfiguration.delayMillis
                    )
                )

                if (expectedTimeInterval != request.timeInterval)
                    throw InvalidTimeIntervalException(request.timeInterval, expectedTimeInterval)

                lastAttestation.dataSignature
            }

    data class Request(val source: Source, val timeInterval: TimeInterval)
}