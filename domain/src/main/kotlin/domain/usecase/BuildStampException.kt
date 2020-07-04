package domain.usecase

import domain.exception.AttestationAlreadyExistsException
import domain.exception.NoDataException
import domain.exception.className
import domain.model.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class BuildStampException @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration
) {
    fun getSingle(source: Source, timeInterval: TimeInterval, exception: Throwable): Single<StampException> =
        Single.fromCallable {
            val now = System.currentTimeMillis()

            val isTimedOut: Boolean =
                (exception is NoDataException &&
                        now - timeInterval.finishIn > attestationConfiguration.tryAgainTimeoutMillis)

            val needsProcess: Boolean =
                !isTimedOut && exception !is AttestationAlreadyExistsException

            StampException(
                timeInterval,
                source,
                exception.className,
                now,
                !needsProcess
            )
        }
}