package br.ufscar.auditchain.common

import br.ufscar.auditchain.common.utils.DAY_MINUTES
import br.ufscar.auditchain.common.utils.minuteInDay
import br.ufscar.auditchain.common.utils.minuteInDayToDateMillis
import br.ufscar.auditchain.common.utils.secondInDay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class TimerNotifier(frequency: Long, private val delay: Long) {

    private val timerSubject: PublishSubject<Pair<Long, Long>> = PublishSubject.create()
    fun getTimerObservable(): Observable<Pair<Long, Long>> = timerSubject

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val instantList: List<Long>

    init {
        val initializeInstantList = mutableListOf<Long>()
        for (temp in frequency until DAY_MINUTES step frequency) {
            initializeInstantList.add(temp)
        }
        initializeInstantList.add(DAY_MINUTES)

        instantList = initializeInstantList
        nextNotify()
    }

    private fun nextNotify() {
        val localNow = LocalDateTime.now()
        val currentZone = ZoneId.systemDefault()
        val zonedNow = ZonedDateTime.of(localNow, currentZone)

        val previousInstant = try {
            instantList.last { it <= zonedNow.minuteInDay }
        } catch (e: NoSuchElementException) {
            instantList.last()
        }

        val nextInstant = try {
            instantList.first { it > zonedNow.minuteInDay }
        } catch (e: NoSuchElementException) {
            instantList.first()
        }

        val delay = (nextInstant * 60) + delay - zonedNow.secondInDay
        println(delay)
        executor.schedule({
            timerSubject.onNext(Pair(zonedNow.minuteInDayToDateMillis(previousInstant), zonedNow.minuteInDayToDateMillis(nextInstant)))
            nextNotify()
        }, delay, TimeUnit.SECONDS)
    }
}