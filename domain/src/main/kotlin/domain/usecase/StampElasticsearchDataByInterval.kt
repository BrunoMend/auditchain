package domain.usecase

import domain.model.Attestation
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class StampElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val stampElasticsearchData: StampElasticsearchData
) : ObservableUseCase<Attestation, StampElasticsearchDataByInterval.Request>() {

    override fun getRawObservable(request: Request): Observable<Attestation> =
        getTimeIntervals.getRawSingle(GetTimeIntervals.Request(request.startAt, request.finishIn))
            .flatMapObservable { Observable.fromIterable(it) }
            .concatMapSingle { stampElasticsearchData.getRawSingle(StampElasticsearchData.Request(it)) }

    data class Request(val startAt: Long, val finishIn: Long)
}