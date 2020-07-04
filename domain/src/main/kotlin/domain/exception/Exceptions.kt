package domain.exception

import domain.model.TimeInterval

class InvalidAttestationException(msg: String? = null) : Exception(msg)
class NoAttestationException(msg: String? = null) : Exception(msg)
class NoDataException(val timeInterval: TimeInterval) : Exception()
class NoOtsDataException(val timeInterval: TimeInterval) : Exception()
class AttestationAlreadyExistsException(val timeInterval: TimeInterval) : Exception()