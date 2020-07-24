package domain.usecase

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class UpdateAttestationsOtsData @Inject constructor(
    private val getNotOtsUpdatedAttestations: GetNotOtsUpdatedAttestations,
    private val updateOtsData: UpdateOtsData
) : CompletableUseCase<Unit>() {

    override fun getRawCompletable(request: Unit): Completable =
        getNotOtsUpdatedAttestations.getRawSingle(Unit)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapCompletable { updateOtsData.getCompletable(UpdateOtsData.Request(it)) }
}