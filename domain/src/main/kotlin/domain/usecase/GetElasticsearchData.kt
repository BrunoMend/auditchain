package domain.usecase

import domain.datarepository.ElasticsearchDataRepository
import domain.exception.NoDataToStampException
import domain.model.AttestationConfiguration
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetElasticsearchData @Inject constructor(
    private val elasticsearchDataRepository: ElasticsearchDataRepository,
    private val attestationConfiguration: AttestationConfiguration
) : SingleUseCase<ByteArray?, GetElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<ByteArray?> =
        elasticsearchDataRepository
            .getElasticsearchData(request.timeInterval)
            .onErrorResumeNext { error ->
                if(error is NoDataToStampException &&
                    System.currentTimeMillis() - request.timeInterval.finishIn > attestationConfiguration.tryAgainTimeoutMillis)
                    null
                else Single.error(error)
            }

    data class Request(val timeInterval: TimeInterval)
}