package domain.usecase

import domain.di.IOScheduler
import domain.model.BlockchainPublication
import domain.utility.Logger
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class VerifyElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val verifyElasticsearchData: VerifyElasticsearchData,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getObservable(startAt: Long, finishIn: Long): Observable<Result<List<BlockchainPublication>>> =
        getTimeIntervals.getSingle(startAt, finishIn)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { verifyElasticsearchData.getSingle(it) }
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}