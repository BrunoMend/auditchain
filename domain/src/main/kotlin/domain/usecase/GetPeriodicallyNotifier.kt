package domain.usecase

import domain.datarepository.ConfigurationDataRepository
import domain.model.Configuration
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

class GetPeriodicallyNotifier(
    configurationDataRepository: ConfigurationDataRepository,
    private val executorScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler
) {

    private val timerSubject: PublishSubject<Pair<Long, Long>> = PublishSubject.create()
    fun getObservable(): Observable<Pair<Long, Long>> = timerSubject.observeOn(postExecutionScheduler)

    private val config: Configuration =
        configurationDataRepository
            .getConfiguration()
            .blockingGet()

    init {
        nextNotify()
    }

    private fun nextNotify() {
        val localNow = LocalDateTime.now()
        val currentZone = ZoneId.systemDefault()
        val zonedNow = ZonedDateTime.of(localNow, currentZone)

        val previousInstant = try {
            config.instants.last { it <= zonedNow.minuteInDay }
        } catch (e: NoSuchElementException) {
            config.instants.last()
        }

        val nextInstant = try {
            config.instants.first { it > zonedNow.minuteInDay }
        } catch (e: NoSuchElementException) {
            config.instants.first()
        }

        val delay = (nextInstant * 60) + config.delay - zonedNow.secondInDay
        executorScheduler.scheduleDirect({
            timerSubject.onNext(
                Pair(
                    zonedNow.minuteInDayToDateMillis(previousInstant),
                    zonedNow.minuteInDayToDateMillis(nextInstant)
                )
            )
            nextNotify()
        }, delay, TimeUnit.SECONDS)
    }
}