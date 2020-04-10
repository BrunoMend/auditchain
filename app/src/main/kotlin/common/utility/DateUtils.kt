package common.utility

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DAY_MINUTES: Long = 1440
const val ELASTIC_DATE_FORMAT: String = "yyyy-MM-dd'T'HH:mm:ss"

fun Date.toElasticDateFormat(pattern: String = ELASTIC_DATE_FORMAT): String = SimpleDateFormat(pattern).format(this)

fun Long.toElasticDateFormat(): String = Date(this).toElasticDateFormat()

fun String.toDate(pattern: String = ELASTIC_DATE_FORMAT): Date = SimpleDateFormat(pattern).parse(this)

fun String.toDateMillis(pattern: String = ELASTIC_DATE_FORMAT): Long = this.toDate(pattern).time

val ZonedDateTime.minuteInDay: Long
    get() = minute + (hour * 60L)

val ZonedDateTime.secondInDay: Long
    get() = (minute + (hour * 60L)) * 60L + second

fun ZonedDateTime.minuteInDayToDateMillis(minutes: Long): Long =
    this.toLocalDateTime()
        .minusMinutes(minuteInDay)
        .minusSeconds(second.toLong())
        .plusMinutes(minutes)
        .format(DateTimeFormatter.ofPattern(ELASTIC_DATE_FORMAT))
        .toDateMillis()
