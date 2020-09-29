package domain.usecase

import domain.exception.AttestationAlreadyExistsException
import domain.exception.errorName
import domain.model.Source
import domain.model.SourceParam
import domain.model.StampException
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class BuildStampException @Inject constructor() : SingleUseCase<StampException, BuildStampException.Request>() {

    override fun getRawSingle(request: Request): Single<StampException> =
        Single.fromCallable {
            val needsProcess: Boolean = request.exception !is AttestationAlreadyExistsException

            StampException(
                request.timeInterval,
                request.source,
                request.sourceParams,
                request.exception.errorName,
                System.currentTimeMillis(),
                !needsProcess
            )
        }

    data class Request(
        val timeInterval: TimeInterval,
        val exception: Throwable,
        val source: Source,
        val sourceParams: Map<SourceParam, String>?
    )
}