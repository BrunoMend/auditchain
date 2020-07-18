package domain.usecase

import domain.model.Attestation
import domain.model.StampException
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ProcessElasticsearchStampException @Inject constructor(
    private val stampElasticsearchData: StampElasticsearchData,
    private val setStampExceptionAsProcessed: SetStampExceptionAsProcessed
) : SingleUseCase<Result<Attestation>, ProcessElasticsearchStampException.Request>() {

    override fun getRawSingle(request: Request): Single<Result<Attestation>> =
        stampElasticsearchData
            .getSingle(StampElasticsearchData.Request(request.stampException.timeInterval))
            .flatMap {
                setStampExceptionAsProcessed
                    .getRawCompletable(SetStampExceptionAsProcessed.Request(request.stampException))
                    .andThen(Single.just(it))
            }

    data class Request(val stampException: StampException)
}