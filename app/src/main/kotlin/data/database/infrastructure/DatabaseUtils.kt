package data.database.infrastructure

private const val KEY_VALUE_SEPARATOR = "=>"
private const val PAIR_SEPARATOR = "||"

fun Map<String, String>.toMapString(): String =
    toList().joinToString(PAIR_SEPARATOR) { "${it.first}$KEY_VALUE_SEPARATOR${it.second}" }

fun String.toMap(): Map<String, String> =
    split(PAIR_SEPARATOR)
        .associate {
            val (key, value) = it.split(KEY_VALUE_SEPARATOR)
            key to value
        }