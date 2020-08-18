package domain.exception

import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import java.util.logging.Level

abstract class ExpectedException(message: String, val loggerLevel: Level) : Exception(message)

class InvalidOriginalDataException(attestation: Attestation) :
    ExpectedException(
        "Original data doesn't match to ots data from interval ${attestation.timeInterval} and source ${attestation.source}",
        Level.SEVERE
    )

class PendingAttestationException(attestation: Attestation) :
    ExpectedException(
        "Data has not yet been stamped from interval ${attestation.timeInterval} and source ${attestation.source}",
        Level.INFO
    )

class BadAttestationException(attestation: Attestation) :
    ExpectedException(
        "Bad attestation from interval ${attestation.timeInterval} and source ${attestation.source}",
        Level.SEVERE
    )

class AttestationAlreadyExistsException(attestation: Attestation) :
    ExpectedException(
        "Attestation already exists from interval ${attestation.timeInterval} and source ${attestation.source}",
        Level.INFO
    )

class NoAttestationException(source: Source, timeInterval: TimeInterval? = null) :
    ExpectedException(
        "No attestation found from source $source" + if (timeInterval != null) " and interval $timeInterval" else "",
        Level.INFO
    )

class NoDataToStampException(timeInterval: TimeInterval, source: Source) :
    ExpectedException("No data to stamp from interval $timeInterval and source $source", Level.INFO)

class MaxTimeIntervalExceededException(timeInterval: TimeInterval) :
    ExpectedException("The maximum time interval has been exceeded by $timeInterval", Level.INFO)

class ServerSideException : ExpectedException("Http Exception on get data", Level.WARNING)

class NoInternetException : ExpectedException("Fail to get data. Verify your internet connection.", Level.WARNING)