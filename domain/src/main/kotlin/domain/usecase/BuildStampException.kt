package domain.usecase

import domain.exception.AttestationAlreadyExistsException
import domain.exception.NoDataToStampException
import domain.exception.errorName
import domain.model.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class BuildStampException @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration
) : SingleUseCase<StampException, BuildStampException.Request>() {

    override fun getRawSingle(request: Request): Single<StampException> =
        Single.fromCallable {
            val now = System.currentTimeMillis()

            val isTimedOut: Boolean =
                (request.exception is NoDataToStampException &&
                        now - request.timeInterval.finishIn > attestationConfiguration.tryAgainTimeoutMillis)

            val needsProcess: Boolean =
                !isTimedOut && request.exception !is AttestationAlreadyExistsException

            StampException(
                request.timeInterval,
                request.source,
                request.exception.errorName,
                now,
                !needsProcess
            )
        }

    data class Request(val source: Source, val timeInterval: TimeInterval, val exception: Throwable)
}