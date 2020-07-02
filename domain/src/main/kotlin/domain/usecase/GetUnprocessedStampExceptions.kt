package domain.usecase

import domain.datarepository.StampExceptionDataRepository
import domain.di.IOScheduler
import domain.model.Source
import domain.model.StampException
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetUnprocessedStampExceptions @Inject constructor(
    private val stampExceptionDataRepository: StampExceptionDataRepository,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getSingle(source: Source): Single<List<StampException>> =
        stampExceptionDataRepository.getUnprocessedStampExceptions(source)
            .doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)
}