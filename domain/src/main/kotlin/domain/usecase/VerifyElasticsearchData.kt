package domain.usecase

import domain.model.AttestationVerifyResult
import domain.model.Source
import domain.model.TimeInterval
import domain.model.TimestampData
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyElasticsearchData @Inject constructor(
    private val getAttestation: GetAttestation,
    private val getElasticsearchData: GetElasticsearchData,
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
                        getElasticsearchData.getRawSingle(
                            GetElasticsearchData.Request(
                                requestData.timeInterval
                            )
                        )
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