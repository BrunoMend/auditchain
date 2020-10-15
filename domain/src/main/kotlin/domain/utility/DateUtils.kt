package domain.utility

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

private const val MILLIS_IN_A_DAY: Long = 86400000
private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
const val UI_DATE_FORMAT = "yyyy-MM-dd HH:mm"

private val zoneOffsetMillis = ZoneOffset.systemDefault().rules.getOffset(Instant.now()).totalSeconds * 1000

fun Date.toDateFormat(pattern: String = DEFAULT_DATE_FORMAT): String = SimpleDateFormat(pattern).format(this)

fun Long.toDateFormat(pattern: String = DEFAULT_DATE_FORMAT): String = Date(this).toDateFormat(pattern)

fun String.toDate(pattern: String = DEFAULT_DATE_FORMAT): Date = SimpleDateFormat(pattern).parse(this)

fun String.toDateMillis(pattern: String = DEFAULT_DATE_FORMAT): Long = this.toDate(pattern).time

fun getMomentMillisOfDay(moment: Long): Long {
    val millisOfDay = moment.rem(MILLIS_IN_A_DAY) + zoneOffsetMillis
    return when {
        millisOfDay < 0 -> millisOfDay + MILLIS_IN_A_DAY
        millisOfDay > MILLIS_IN_A_DAY -> millisOfDay - MILLIS_IN_A_DAY
        else -> millisOfDay
    }
}

fun getNextTimeInterval(moment: Long, frequencyMillis: Long, disregardCurrent: Boolean = true): Long {
    val momentMillisOfDay = getMomentMillisOfDay(moment)
    val momentDayMidnight = moment - momentMillisOfDay
    val millisToAdd = momentMillisOfDay.rem(frequencyMillis)
    return when {
        momentMillisOfDay > (MILLIS_IN_A_DAY - frequencyMillis) -> momentDayMidnight + MILLIS_IN_A_DAY
        millisToAdd == 0L -> if (disregardCurrent) moment + frequencyMillis else moment
        else -> moment - millisToAdd + frequencyMillis
    }
}

fun getPreviousTimeInterval(moment: Long, frequencyMillis: Long, disregardCurrent: Boolean = true): Long {
    val momentMillisOfDay = getMomentMillisOfDay(moment)
    val momentDayMidnight = moment - momentMillisOfDay
    val millisToDeduct = momentMillisOfDay.rem(frequencyMillis)
    return when {
        momentMillisOfDay < frequencyMillis -> momentDayMidnight
        millisToDeduct == 0L -> if (disregardCurrent) moment - frequencyMillis else moment
        else -> moment - millisToDeduct
    }
}