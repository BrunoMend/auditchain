package domain.utility

fun Map<*, *>?.toKeyValueString(): String = this?.map { "${it.key} : ${it.value}" }?.joinToString("\n") ?: ""