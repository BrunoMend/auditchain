package domain.usecase

import domain.datarepository.ElasticsearchDataRepository
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetElasticsearchData @Inject constructor(
    private val elasticsearchDataRepository: ElasticsearchDataRepository
) : SingleUseCase<ByteArray?, GetElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<ByteArray?> =
        elasticsearchDataRepository.getElasticsearchData(request.indexPattern, request.timeInterval)

    data class Request(val indexPattern: String, val timeInterval: TimeInterval)
}