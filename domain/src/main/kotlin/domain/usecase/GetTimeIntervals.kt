package domain.usecase

import domain.exception.MaxTimeIntervalExceededException
import domain.model.AttestationConfiguration
import domain.model.TimeInterval
import domain.utility.getNextTimeInterval
import domain.utility.getPreviousTimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetTimeIntervals @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration
) : SingleUseCase<List<TimeInterval>, GetTimeIntervals.Request>() {

    override fun getRawSingle(request: Request): Single<List<TimeInterval>> =
        Single.fromCallable<List<TimeInterval>> {
            val firstMomentInterval: Long =
                getPreviousTimeInterval(request.startAt, attestationConfiguration.frequencyMillis, false)
            val finishMomentInterval: Long =
                getNextTimeInterval(request.finishIn, attestationConfiguration.frequencyMillis, false)

            if (finishMomentInterval - firstMomentInterval > attestationConfiguration.maxTimeIntervalMillis)
                throw MaxTimeIntervalExceededException(TimeInterval(firstMomentInterval, finishMomentInterval))

            val intervalList = mutableListOf<TimeInterval>()
            var interval: Long = firstMomentInterval
            while (interval < finishMomentInterval) {
                val nextInterval = getNextTimeInterval(interval, attestationConfiguration.frequencyMillis)
                intervalList.add(TimeInterval(interval, nextInterval))
                interval = nextInterval
            }

            intervalList
        }

    data class Request(val startAt: Long, val finishIn: Long)
}