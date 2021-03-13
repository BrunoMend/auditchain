package domain.usecase

import domain.datarepository.ElasticsearchDataRepository
import domain.exception.errorName
import domain.model.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampElasticsearchData @Inject constructor(
    private val getLastAttestationDataSignatureOrDefault: GetLastAttestationDataSignatureOrDefault,
    private val elasticsearchDataRepository: ElasticsearchDataRepository,
    private val stampData: StampData,
    private val saveAttestation: SaveAttestation,
    private val saveStampException: SaveStampException,
) : SingleUseCase<Attestation, StampElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<Attestation> =
        getLastAttestationDataSignatureOrDefault
            .getSingle(
                GetLastAttestationDataSignatureOrDefault.Request(
                    Source.ELASTICSEARCH,
                    request.timeInterval,
                )
            ).flatMap { lastAttestationDataSignature ->
                elasticsearchDataRepository.getElasticsearchData(request.timeInterval)
                    .map { it.plus(lastAttestationDataSignature) }
            }
            .flatMap { data ->
                stampData.getRawSingle(StampData.Request(TimestampData(request.timeInterval, data)))
                    .flatMap { timestampResult ->
                        val attestation =
                            Attestation(
                                request.timeInterval,
                                Source.ELASTICSEARCH,
                                System.currentTimeMillis(),
                                timestampResult.dataSignature,
                                timestampResult.otsData
                            )
                        saveAttestation.getRawCompletable(SaveAttestation.Request(attestation))
                            .andThen(Single.just(attestation))
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