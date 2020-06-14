package domain.usecase

import domain.datarepository.ConfigurationDataRepository
import domain.di.ComputationScheduler
import domain.model.AttestationConfiguration
import domain.model.TimeInterval
import domain.utility.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Deprecated("If not used soon it will be deleted")
class GetTimerNotifier @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration,
    @ComputationScheduler private val executorScheduler: Scheduler
) {
    private val ZonedDateTime.minuteInDay: Long
        get() = minute + (hour * 60L)

    private val ZonedDateTime.secondInDay: Long
        get() = (minute + (hour * 60L)) * 60L + second

    private fun ZonedDateTime.minuteInDayToDateMillis(minutes: Long): Long =
        this.toLocalDateTime()
            .minusMinutes(minuteInDay)
            .minusSeconds(second.toLong())
            .plusMinutes(minutes)
            .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT))
            .toDateMillis()

    private val timerSubject: PublishSubject<TimeInterval> = PublishSubject.create()
    fun getObservable(): Observable<TimeInterval> = timerSubject

    init {
        nextNotify()
    }

    private fun nextNotify() {
        val localNow = LocalDateTime.now()
        val currentZone = ZoneId.systemDefault()
        val zonedNow = ZonedDateTime.of(localNow, currentZone)

        val previousInstant = try {
            attestationConfiguration.timeIntervalList.last { it <= zonedNow.minuteInDay }
        } catch (e: NoSuchElementException) {
            attestationConfiguration.timeIntervalList.last()
        }

        val nextInstant = try {
            attestationConfiguration.timeIntervalList.first { it > zonedNow.minuteInDay }
        } catch (e: NoSuchElementException) {
            attestationConfiguration.timeIntervalList.first()
        }

        val delay = (nextInstant * 60) + attestationConfiguration.delay - zonedNow.secondInDay
        executorScheduler.scheduleDirect({
            timerSubject.onNext(
                TimeInterval(
                    zonedNow.minuteInDayToDateMillis(previousInstant),
                    zonedNow.minuteInDayToDateMillis(nextInstant)
                )
            )
            nextNotify()
        }, delay, TimeUnit.SECONDS)
    }
}