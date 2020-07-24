package domain.exception

import java.util.logging.Logger

val Throwable.className: String
    get() = this::class.qualifiedName ?: "java.rmi.UnexpectedException"

fun Throwable.log(logger: Logger) {
    when (this) {
        is ExpectedException -> logger.log(this.loggerLevel, this.message)
        else -> logger.warning("Unexpected error: ${this.className} :: ${this.message}")
    }
}