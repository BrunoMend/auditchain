package domain.usecase

import domain.exception.TimeShorterThanCurrentWithDelayException
import domain.model.AttestationConfiguration
import domain.model.TimeInterval
import domain.utility.getNextTimeInterval
import domain.utility.getPreviousTimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Return a TimeInterval list from intervals between a given start and finish time
 */
class GetTimeIntervals @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration
) : SingleUseCase<List<TimeInterval>, GetTimeIntervals.Request>() {

    override fun getRawSingle(request: Request): Single<List<TimeInterval>> =

        Single.fromCallable<List<TimeInterval>> {
            val firstMomentInterval: Long =
                getPreviousTimeInterval(
                    request.startAt,
                    attestationConfiguration.frequencyMillis,
                    attestationConfiguration.delayMillis,
                    false
                )
            val finishMomentInterval: Long =
                getNextTimeInterval(
                    request.finishIn,
                    attestationConfiguration.frequencyMillis,
                    attestationConfiguration.delayMillis,
                    false
                )

            val intervalList = mutableListOf<TimeInterval>()
            var interval: Long = firstMomentInterval
            while (interval < finishMomentInterval) {
                try {
                    val nextInterval = getNextTimeInterval(
                        interval,
                        attestationConfiguration.frequencyMillis,
                        attestationConfiguration.delayMillis
                    )
                    intervalList.add(TimeInterval(interval, nextInterval))
                    interval = nextInterval
                } catch (e: TimeShorterThanCurrentWithDelayException) {
                    //get this error here is not a problem, just ignore
                }
            }

            intervalList
        }

    data class Request(val startAt: Long, val finishIn: Long)
}