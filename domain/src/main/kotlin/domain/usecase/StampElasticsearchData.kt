package domain.usecase

import domain.model.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampElasticsearchData @Inject constructor(
    private val validateNoAttestationExists: ValidateNoAttestationExists,
    private val getElasticsearchData: GetElasticsearchData,
    private val stampData: StampData,
    private val saveAttestation: SaveAttestation,
    private val saveStampException: SaveStampException,
    private val buildStampException: BuildStampException
) : SingleUseCase<Attestation, StampElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<Attestation> =
        Single.just(request)
            .flatMap { requestData ->
                validateNoAttestationExists
                    .getCompletable(
                        ValidateNoAttestationExists.Request(
                            Source.ELASTICSEARCH,
                            requestData.timeInterval,
                            mapOf(Pair(SourceParam.INDEX_PATTERN, requestData.indexPattern))
                        )
                    )
                    .andThen(
                        getElasticsearchData.getRawSingle(
                            GetElasticsearchData.Request(
                                requestData.indexPattern,
                                requestData.timeInterval
                            )
                        )
                    )
                    .flatMap { data ->
                        stampData.getRawSingle(StampData.Request(TimestampData(requestData.timeInterval, data)))
                            .flatMap { timestampResult ->
                                val attestation =
                                    Attestation(
                                        requestData.timeInterval,
                                        Source.ELASTICSEARCH,
                                        mapOf(Pair(SourceParam.INDEX_PATTERN, requestData.indexPattern)),
                                        System.currentTimeMillis(),
                                        timestampResult.dataSignature,
                                        timestampResult.otsData
                                    )
                                saveAttestation.getRawCompletable(SaveAttestation.Request(attestation))
                                    .andThen(Single.just(attestation))
                            }
                    }
            }
            .doOnError { error ->
                buildStampException.getRawSingle(
                    BuildStampException.Request(
                        request.timeInterval,
                        error,
                        Source.ELASTICSEARCH,
                        mapOf(Pair(SourceParam.INDEX_PATTERN, request.indexPattern))
                    )
                )
                    .flatMapCompletable { saveStampException.getRawCompletable(SaveStampException.Request(it)) }
            }

    data class Request(val indexPattern: String, val timeInterval: TimeInterval)
}