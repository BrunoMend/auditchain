package domain.usecase

import domain.exception.AttestationAlreadyExistsException
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class ValidateNoAttestationExists @Inject constructor(
    private val getAttestation: GetAttestation
) : CompletableUseCase<ValidateNoAttestationExists.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        getAttestation.getRawSingle(GetAttestation.Request(request.source, request.timeInterval))
            .flatMapCompletable { throw AttestationAlreadyExistsException(request.timeInterval) }
            .onErrorComplete { it is NoSuchElementException }

    data class Request(val source: Source, val timeInterval: TimeInterval)
}