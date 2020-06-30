package domain.exception

import domain.model.TimeInterval

class InvalidAttestationException(msg: String? = null): Exception(msg)
class NoDataException(val timeInterval: TimeInterval): Exception()