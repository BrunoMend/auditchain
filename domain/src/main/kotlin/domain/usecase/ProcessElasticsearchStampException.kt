package domain.usecase

import domain.model.Attestation
import domain.model.SourceParam
import domain.model.StampException
import io.reactivex.rxjava3.core.Single
import java.lang.IllegalStateException
import javax.inject.Inject

class ProcessElasticsearchStampException @Inject constructor(
    private val stampElasticsearchData: StampElasticsearchData,
    private val setStampExceptionAsProcessed: SetStampExceptionAsProcessed
) : SingleUseCase<Result<Attestation>, ProcessElasticsearchStampException.Request>() {

    override fun getRawSingle(request: Request): Single<Result<Attestation>> {
        val indexPattern = request.stampException.sourceParams?.get(SourceParam.INDEX_PATTERN)
            ?: throw IllegalStateException("${SourceParam.INDEX_PATTERN} is required")
        return stampElasticsearchData
            .getSingle(StampElasticsearchData.Request(indexPattern, request.stampException.timeInterval))
            .flatMap {
                setStampExceptionAsProcessed
                    .getRawCompletable(SetStampExceptionAsProcessed.Request(request.stampException))
                    .andThen(Single.just(it))
            }
    }

    data class Request(val stampException: StampException)
}