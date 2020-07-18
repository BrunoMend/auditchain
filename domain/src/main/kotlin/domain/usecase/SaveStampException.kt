package domain.usecase

import domain.datarepository.StampExceptionDataRepository
import domain.model.StampException
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SaveStampException @Inject constructor(
    private val stampExceptionDataRepository: StampExceptionDataRepository
) : CompletableUseCase<SaveStampException.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        stampExceptionDataRepository.saveStampException(request.stampException)

    data class Request(val stampException: StampException)
}