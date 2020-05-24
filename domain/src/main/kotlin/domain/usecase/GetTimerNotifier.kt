package domain.usecase

import domain.datarepository.ConfigurationDataRepository
import domain.di.ComputationScheduler
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
import javax.inject.Inject

class GetTimerNotifier @Inject constructor(
    private val configurationDataRepository: ConfigurationDataRepository,
    @ComputationScheduler private val executorScheduler: Scheduler
) {

    private val timerSubject: PublishSubject<TimeInterval> = PublishSubject.create()
    fun getObservable(): Observable<TimeInterval> = timerSubject

    //TODO remove blockingGet
    private val attestationConfiguration: AttestationConfiguration by lazy {
        configurationDataRepository.getAttestationConfiguration().blockingGet()
    }

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