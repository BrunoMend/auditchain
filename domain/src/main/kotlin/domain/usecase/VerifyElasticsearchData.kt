package domain.usecase

import domain.model.BlockchainPublication
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyElasticsearchData @Inject constructor(
    private val getAttestation: GetAttestation,
    private val getElasticsearchData: GetElasticsearchData,
    private val verifyStamp: VerifyStamp
) : SingleUseCase<Result<List<BlockchainPublication>>, VerifyElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<Result<List<BlockchainPublication>>> =
        Single.just(request.timeInterval)
            .flatMap { timeInterval ->
                getAttestation.getRawSingle(GetAttestation.Request(Source.ELASTICSEARCH, timeInterval))
                    .flatMap { attestation ->
                        getElasticsearchData.getRawSingle(GetElasticsearchData.Request(timeInterval))
                            .flatMap { originalData ->
                                verifyStamp
                                    .getSingle(VerifyStamp.Request(originalData, attestation.otsData))
                                    .map { Result.success(it) }
                            }
                    }
            }
            .onErrorResumeNext { Single.just(Result.failure(it)) }

    data class Request(val timeInterval: TimeInterval)
}