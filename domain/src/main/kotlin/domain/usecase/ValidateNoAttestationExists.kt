package domain.usecase

import domain.exception.AttestationAlreadyExistsException
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class ValidateNoAttestationExists @Inject constructor(
    private val getAttestation: GetAttestation
) {
    fun getCompletable(source: Source, timeInterval: TimeInterval): Completable =
        getAttestation.getSingle(source, timeInterval)
            .flatMapCompletable { throw AttestationAlreadyExistsException(timeInterval) }
            .onErrorComplete { it is NoSuchElementException }
}