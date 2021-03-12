package domain.usecase

import domain.datarepository.ElasticsearchDataRepository
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetConcatenatedElasticsearchData @Inject constructor(
    private val getLastAttestationDataSignatureOrDefault: GetLastAttestationDataSignatureOrDefault,
    private val elasticsearchDataRepository: ElasticsearchDataRepository
) : SingleUseCase<ByteArray, GetConcatenatedElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<ByteArray> =
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

    data class Request(val timeInterval: TimeInterval)
}