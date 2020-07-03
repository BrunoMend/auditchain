package domain.usecase

import domain.di.IOScheduler
import domain.exception.AttestationAlreadyExistsException
import domain.model.Source
import domain.model.TimeInterval
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class ValidateNoAttestationExists @Inject constructor(
    private val getAttestation: GetAttestation,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(source: Source, timeInterval: TimeInterval): Completable =
        getAttestation.getSingle(source, timeInterval)
            .flatMapCompletable { throw AttestationAlreadyExistsException(timeInterval) }
            .onErrorComplete { it is NoSuchElementException }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}