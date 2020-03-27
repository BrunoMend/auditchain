package br.ufscar.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

fun Date.toElasticDateFormat(): String = dateFormat.format(this)

fun Long.toElasticDateFormat(): String = Date(this).toElasticDateFormat()

fun String.toDate(): Date = dateFormat.parse(this)

fun String.toDateMillis(): Long = this.toDate().time