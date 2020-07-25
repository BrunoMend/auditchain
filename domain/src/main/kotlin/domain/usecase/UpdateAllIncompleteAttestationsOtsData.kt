package domain.usecase

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class UpdateAllIncompleteAttestationsOtsData @Inject constructor(
    private val getIncompleteOtsAttestations: GetIncompleteOtsAttestations,
    private val updateAttestationOtsData: UpdateAttestationOtsData
) : CompletableUseCase<Unit>() {

    override fun getRawCompletable(request: Unit): Completable =
        getIncompleteOtsAttestations.getRawSingle(Unit)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapCompletable { updateAttestationOtsData.getCompletable(UpdateAttestationOtsData.Request(it)) }
}