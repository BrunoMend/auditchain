package domain.usecase

import domain.datarepository.ElasticsearchDataRepository
import domain.model.AttestationVerifyResult
import domain.model.Source
import domain.model.TimeInterval
import domain.model.TimestampData
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyElasticsearchData @Inject constructor(
    private val getAttestation: GetAttestation,
    private val getPreviousAttestationSignature: GetPreviousAttestationSignature,
    private val elasticsearchDataRepository: ElasticsearchDataRepository,
    private val verifyStamp: VerifyStamp
) : SingleUseCase<Result<AttestationVerifyResult>, VerifyElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<Result<AttestationVerifyResult>> =
        Single.just(request)
            .flatMap { requestData ->
                getAttestation.getSingle(
                    GetAttestation.Request(
                        Source.ELASTICSEARCH,
                        requestData.timeInterval
                    )
                )
                    .flatMap { attestation ->
                        getPreviousAttestationSignature
                            .getSingle(
                                GetPreviousAttestationSignature.Request(
                                    Source.ELASTICSEARCH,
                                    request.timeInterval,
                                )
                            ).flatMap { lastAttestationDataSignature ->
                                elasticsearchDataRepository.getElasticsearchData(request.timeInterval)
                                    .map { it.plus(lastAttestationDataSignature) }
                            }
                            .flatMap { originalData ->
                                verifyStamp
                                    .getSingle(
                                        VerifyStamp.Request(
                                            TimestampData(requestData.timeInterval, originalData),
                                            attestation
                                        )
                                    )
                                    .map { Result.success(AttestationVerifyResult(attestation, it)) }
                            }
                    }
            }.onErrorResumeNext { Single.just(Result.failure(it)) }

    data class Request(val timeInterval: TimeInterval)
}