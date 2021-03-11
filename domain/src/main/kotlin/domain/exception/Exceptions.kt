package domain.exception

import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import java.util.logging.Level

abstract class ExpectedException(message: String, val loggerLevel: Level) : Exception(message)

class InvalidOriginalDataException(attestation: Attestation) :
    ExpectedException(
        "Original data doesn't match to ots data from:\n$attestation",
        Level.SEVERE
    )

class InvalidDataSignatureException :
    ExpectedException(
        "Data signature cannot be verified with the verify key",
        Level.SEVERE
    )

class PendingAttestationException(attestation: Attestation) :
    ExpectedException(
        "Data attested by OpenTimestamps and pending Bitcoin attestation from:\n$attestation",
        Level.INFO
    )

class BadAttestationException(attestation: Attestation) :
    ExpectedException(
        "Bad attestation from:\n$attestation",
        Level.SEVERE
    )

class InvalidTimeIntervalException(wrongTimeInterval: TimeInterval, expectedTimeInterval: TimeInterval) :
    ExpectedException(
        "The time interval $wrongTimeInterval is wrong. The expected time interval is $expectedTimeInterval",
        Level.WARNING
    )

class NoAttestationException(source: Source, timeInterval: TimeInterval?) :
    ExpectedException(
        "No attestation found from:\n" +
                (if (timeInterval != null) "Interval: $timeInterval\n" else "") +
                "Source $source\n",
        Level.INFO
    )

class HttpServerException(code: Int) :
    ExpectedException("Server error on get data. Status-code: $code", Level.WARNING)

class HttpClientException(code: Int) :
    ExpectedException("Client error on get data. Status-code: $code", Level.WARNING)

class NoInternetException : ExpectedException("Fail to get data. Verify your internet connection.", Level.WARNING)

class TimeShorterThanCurrentWithDelayException(momentTime: Long, momentTimeWithDelay: Long) : ExpectedException(
    "Stamp data from start at ${momentTime.toDateFormat(UI_DATE_FORMAT)} " +
            "must be called after ${(momentTimeWithDelay).toDateFormat(UI_DATE_FORMAT)}",
    Level.WARNING
)