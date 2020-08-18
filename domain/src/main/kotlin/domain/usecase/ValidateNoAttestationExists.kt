package domain.usecase

import domain.exception.AttestationAlreadyExistsException
import domain.exception.NoAttestationException
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class ValidateNoAttestationExists @Inject constructor(
    private val getAttestation: GetAttestation
) : CompletableUseCase<ValidateNoAttestationExists.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        getAttestation.getRawSingle(GetAttestation.Request(request.source, request.timeInterval))
            .flatMapCompletable { throw AttestationAlreadyExistsException(it) }
            .onErrorComplete { it is NoAttestationException }

    data class Request(val source: Source, val timeInterval: TimeInterval)
}