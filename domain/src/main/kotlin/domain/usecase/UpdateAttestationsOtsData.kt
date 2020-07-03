package domain.usecase

import domain.di.IOScheduler
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class UpdateAttestationsOtsData @Inject constructor(
    private val getNotOtsUpdatedAttestations: GetNotOtsUpdatedAttestations,
    private val updateOtsData: UpdateOtsData,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(): Completable =
        getNotOtsUpdatedAttestations.getSingle()
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapCompletable { updateOtsData.getCompletable(it) }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}