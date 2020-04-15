package domain.usecase

import domain.model.AttestationConfiguration
import domain.model.TimeInterval
import domain.utility.minuteInDay
import domain.utility.minuteInDayToDateMillis
import domain.utility.secondInDay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class GetTimerNotifier(
    private val attestationConfiguration: AttestationConfiguration,
    private val executorScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler
) {

    private val timerSubject: PublishSubject<TimeInterval> = PublishSubject.create()
    fun getObservable(): Observable<TimeInterval> = timerSubject.observeOn(postExecutionScheduler)

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