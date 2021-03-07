package domain.utility

import domain.exception.TimeShorterThanCurrentWithDelayException
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

/**
 * Get the next time interval from the moment given the frequencyMillis and the delayMillis
 */
fun getNextTimeInterval(
    moment: Long,
    frequencyMillis: Long,
    delayMillis: Long,
    disregardCurrent: Boolean = true
): Long {
    val momentMillisOfDay = getMomentMillisOfDay(moment)
    val momentDayMidnight = moment - momentMillisOfDay
    val millisToAdd = momentMillisOfDay.rem(frequencyMillis)
    val resultMoment = when {
        momentMillisOfDay > (MILLIS_IN_A_DAY - frequencyMillis) -> momentDayMidnight + MILLIS_IN_A_DAY
        millisToAdd == 0L -> if (disregardCurrent) moment + frequencyMillis else moment
        else -> moment - millisToAdd + frequencyMillis
    }
    validateMomentWithDelay(resultMoment, delayMillis)
    return resultMoment
}

/**
 * Get the previous time interval from the moment given the frequencyMillis and the delayMillis
 */
fun getPreviousTimeInterval(
    moment: Long,
    frequencyMillis: Long,
    delayMillis: Long,
    disregardCurrent: Boolean = true
): Long {
    val momentMillisOfDay = getMomentMillisOfDay(moment)
    val momentDayMidnight = moment - momentMillisOfDay
    val millisToDeduct = momentMillisOfDay.rem(frequencyMillis)
    val resultMoment = when {
        momentMillisOfDay < frequencyMillis -> momentDayMidnight
        millisToDeduct == 0L -> if (disregardCurrent) moment - frequencyMillis else moment
        else -> moment - millisToDeduct
    }
    validateMomentWithDelay(resultMoment, delayMillis)
    return resultMoment
}

/**
 * Validate if the momentMillis is less than current moment with the delayMillis
 */
fun validateMomentWithDelay(momentMillis: Long, delayMillis: Long) {
    val momentWithDelayMillis = momentMillis + delayMillis
    if (momentWithDelayMillis > System.currentTimeMillis())
        throw TimeShorterThanCurrentWithDelayException(momentMillis, momentWithDelayMillis)
}