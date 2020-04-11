package common.utility

import java.text.SimpleDateFormat
import java.util.*

const val ELASTIC_DATE_FORMAT: String = "yyyy-MM-dd'T'HH:mm:ss"

fun Date.toElasticDateFormat(pattern: String = ELASTIC_DATE_FORMAT): String = SimpleDateFormat(pattern).format(this)

fun Long.toElasticDateFormat(): String = Date(this).toElasticDateFormat()
