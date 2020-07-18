package domain.exception

import java.util.logging.Logger

val Throwable.className: String
    get() = this::class.qualifiedName ?: "java.rmi.UnexpectedException"

//TODO improve messages
fun Throwable.log(logger: Logger) {
    when (this) {
        is InvalidAttestationException -> logger.warning("Pending or bad attestation")
        is NoAttestationException -> logger.warning("No attestation founded")
        is AttestationAlreadyExistsException -> logger.warning("Attempt to restamp data")
        is NoDataException -> logger.info("No data to stamp")
        is MaxTimeIntervalExceededException -> logger.info("Attempt to stamp data from longer interval than configured")
    }
}