package domain.usecase

import domain.model.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyElasticsearchData @Inject constructor(
    private val getAttestation: GetAttestation,
    private val getElasticsearchData: GetElasticsearchData,
    private val verifyStamp: VerifyStamp
) : SingleUseCase<Result<Pair<TimeInterval, List<BlockchainPublication>>>, VerifyElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<Result<Pair<TimeInterval, List<BlockchainPublication>>>> =
        Single.just(request)
            .flatMap { requestData ->
                getAttestation.getSingle(
                    GetAttestation.Request(
                        Source.ELASTICSEARCH,
                        requestData.timeInterval,
                        mapOf(Pair(SourceParam.INDEX_PATTERN, requestData.indexPattern))
                    )
                )
                    .flatMap { attestation ->
                        getElasticsearchData.getRawSingle(
                            GetElasticsearchData.Request(
                                requestData.indexPattern,
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
                                    .map { Result.success(Pair(requestData.timeInterval, it)) }
                            }
                    }
            }.onErrorResumeNext { Single.just(Result.failure(it)) }

    data class Request(val indexPattern: String, val timeInterval: TimeInterval)
}