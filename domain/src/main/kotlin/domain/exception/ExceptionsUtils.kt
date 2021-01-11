package domain.exception

import java.util.logging.Level

val Throwable.errorName: String
    get() = this::class.qualifiedName ?: "UnexpectedException"

val Throwable.logMessage: String
    get() = when (this) {
        is ExpectedException -> this.message!!
        else -> "Unexpected error: ${this.errorName} :: ${this.message}:" +
                "\n${this.stackTraceToString()}" +
                "\n--------------------------------------------------\n"
    }

val Throwable.logLevel: Level
    get() = when (this) {
        is ExpectedException -> this.loggerLevel
        else -> Level.WARNING
    }