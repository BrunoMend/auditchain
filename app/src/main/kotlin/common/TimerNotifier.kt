package common
//
//import common.utility.minuteInDay
//import common.utility.minuteInDayToDateMillis
//import common.utility.secondInDay
//import io.reactivex.rxjava3.core.Observable
//import io.reactivex.rxjava3.schedulers.Schedulers
//import io.reactivex.rxjava3.subjects.PublishSubject
//import java.time.LocalDateTime
//import java.time.ZoneId
//import java.time.ZonedDateTime
//import java.util.concurrent.TimeUnit
//
//internal class TimerNotifier {
//
//    private val timerSubject: PublishSubject<Pair<Long, Long>> = PublishSubject.create()
//    fun getTimerObservable(): Observable<Pair<Long, Long>> = timerSubject
//
//    private val timerScheduler = Schedulers.newThread()
//
//    init {
//        nextNotify()
//    }
//
//    private fun nextNotify() {
//        val localNow = LocalDateTime.now()
//        val currentZone = ZoneId.systemDefault()
//        val zonedNow = ZonedDateTime.of(localNow, currentZone)
//
//        val previousInstant = try {
//            Config.instants.last { it <= zonedNow.minuteInDay }
//        } catch (e: NoSuchElementException) {
//            Config.instants.last()
//        }
//
//        val nextInstant = try {
//            Config.instants.first { it > zonedNow.minuteInDay }
//        } catch (e: NoSuchElementException) {
//            Config.instants.first()
//        }
//
//        val delay = (nextInstant * 60) + Config.delay - zonedNow.secondInDay
//        timerScheduler.scheduleDirect({
//            timerSubject.onNext(
//                Pair(
//                    zonedNow.minuteInDayToDateMillis(previousInstant),
//                    zonedNow.minuteInDayToDateMillis(nextInstant)
//                )
//            )
//            nextNotify()
//        }, delay, TimeUnit.SECONDS)
//    }
//}