package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.model.BlockchainPublication
import domain.model.TimestampData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyStamp @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository,
    private val attestationConfiguration: AttestationConfiguration
) : SingleUseCase<List<BlockchainPublication>, VerifyStamp.Request>() {

    override fun getRawSingle(request: Request): Single<List<BlockchainPublication>> =
        Completable.fromAction {
            request.timestampData.verifySignature(attestationConfiguration.verifyKey, request.attestation.dataSignature)
        }.andThen(timestampDataRepository.verifyStamp(request.attestation))

    data class Request(
        val timestampData: TimestampData,
        val attestation: Attestation
    )
}