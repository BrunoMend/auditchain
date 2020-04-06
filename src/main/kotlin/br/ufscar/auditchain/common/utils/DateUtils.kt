package br.ufscar.auditchain.common.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DAY_MINUTES: Long = 1440

val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

fun Date.toElasticDateFormat(): String = dateFormat.format(this)

fun Long.toElasticDateFormat(): String = Date(this).toElasticDateFormat()

fun String.toDate(): Date = dateFormat.parse(this)

fun String.toDateMillis(): Long = this.toDate().time

val ZonedDateTime.minuteInDay: Long
    get() = minute + (hour * 60L)

val ZonedDateTime.secondInDay: Long
    get() = (minute + (hour * 60L)) * 60L + second

fun ZonedDateTime.minuteInDayToDateMillis(minutes: Long): Long =
    this.toLocalDateTime()
        .minusMinutes(minuteInDay)
        .minusSeconds(second.toLong())
        .plusMinutes(minutes)
        .format(dateTimeFormatter)
        .toDateMillis()
