package domain.usecase

import domain.model.BlockchainPublication
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class VerifyElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val verifyElasticsearchData: VerifyElasticsearchData
) : ObservableUseCase<Result<Pair<TimeInterval, List<BlockchainPublication>>>, VerifyElasticsearchDataByInterval.Request>() {

    override fun getRawObservable(request: Request): Observable<Result<Pair<TimeInterval, List<BlockchainPublication>>>> =
        getTimeIntervals.getRawSingle(GetTimeIntervals.Request(request.startAt, request.finishIn))
            .flatMapObservable { Observable.fromIterable(it) }
            .concatMapSingle { verifyElasticsearchData.getSingle(VerifyElasticsearchData.Request(it)) }

    data class Request(val startAt: Long, val finishIn: Long)
}