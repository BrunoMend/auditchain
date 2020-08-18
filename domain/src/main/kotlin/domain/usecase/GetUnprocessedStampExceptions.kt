package domain.usecase

import domain.datarepository.StampExceptionDataRepository
import domain.model.Source
import domain.model.StampException
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetUnprocessedStampExceptions @Inject constructor(
    private val stampExceptionDataRepository: StampExceptionDataRepository
) : SingleUseCase<List<StampException>, GetUnprocessedStampExceptions.Request>() {

    override fun getRawSingle(request: Request): Single<List<StampException>> =
        stampExceptionDataRepository.getUnprocessedStampExceptions(request.source)

    data class Request(val source: Source)
}