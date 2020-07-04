package domain.exception

import domain.model.TimeInterval

class InvalidAttestationException(msg: String? = null) : Exception(msg)
class NoAttestationException(msg: String? = null) : Exception(msg)
class AttestationAlreadyExistsException(val timeInterval: TimeInterval) : Exception()

class NoDataException(val timeInterval: TimeInterval) : Exception()
class NoOtsDataException(val timeInterval: TimeInterval) : Exception()

class MaxTimeIntervalExceededException(val timeInterval: TimeInterval) : Exception()