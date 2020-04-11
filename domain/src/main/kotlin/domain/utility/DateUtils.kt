package domain.utility

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

fun String.toDate(pattern: String = DEFAULT_DATE_FORMAT): Date = SimpleDateFormat(pattern).parse(this)

fun String.toDateMillis(pattern: String = DEFAULT_DATE_FORMAT): Long = this.toDate(pattern).time

val ZonedDateTime.minuteInDay: Long
    get() = minute + (hour * 60L)

val ZonedDateTime.secondInDay: Long
    get() = (minute + (hour * 60L)) * 60L + second

fun ZonedDateTime.minuteInDayToDateMillis(minutes: Long): Long =
    this.toLocalDateTime()
        .minusMinutes(minuteInDay)
        .minusSeconds(second.toLong())
        .plusMinutes(minutes)
        .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT))
        .toDateMillis()
