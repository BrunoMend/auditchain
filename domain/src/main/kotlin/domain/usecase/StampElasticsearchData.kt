package domain.usecase

import domain.exception.errorName
import domain.model.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampElasticsearchData @Inject constructor(
    private val getLastAttestationDataSignature: GetLastAttestationDataSignature,
    private val getElasticsearchData: GetElasticsearchData,
    private val stampData: StampData,
    private val saveAttestation: SaveAttestation,
    private val saveStampException: SaveStampException,
) : SingleUseCase<Attestation, StampElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<Attestation> =
        Single.just(request)
            .flatMap { requestData ->
                getLastAttestationDataSignature
                    .getSingle(
                        GetLastAttestationDataSignature.Request(
                            Source.ELASTICSEARCH,
                            requestData.timeInterval,
                        )
                    )
                    .flatMap { lastAttestationDataSignature ->
                        getElasticsearchData.getRawSingle(
                            GetElasticsearchData.Request(
                                requestData.timeInterval
                            )
                        ).map { it.plus(lastAttestationDataSignature) }
                    }
                    .flatMap { data ->
                        stampData.getRawSingle(StampData.Request(TimestampData(requestData.timeInterval, data)))
                            .flatMap { timestampResult ->
                                val attestation =
                                    Attestation(
                                        requestData.timeInterval,
                                        Source.ELASTICSEARCH,
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
                saveStampException.getRawCompletable(
                    SaveStampException.Request(
                        StampException(
                            request.timeInterval,
                            Source.ELASTICSEARCH,
                            error.errorName,
                            System.currentTimeMillis(),
                        )
                    )
                )
            }

    data class Request(val timeInterval: TimeInterval)
}