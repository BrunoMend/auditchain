package domain.exception

import java.util.logging.Logger

val Throwable.errorName: String
    get() = this::class.qualifiedName ?: "UnexpectedException"

fun Throwable.log(logger: Logger) {
    when (this) {
        is ExpectedException -> logger.log(this.loggerLevel, this.message)
        else -> logger.warning("Unexpected error: ${this.errorName} :: ${this.message}")
    }
}