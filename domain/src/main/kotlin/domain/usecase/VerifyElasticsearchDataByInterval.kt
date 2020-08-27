package domain.usecase

import domain.model.BlockchainPublication
import domain.model.ElasticsearchConfiguration
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class VerifyElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val verifyElasticsearchData: VerifyElasticsearchData,
    private val elasticsearchConfiguration: ElasticsearchConfiguration
) : ObservableUseCase<Result<Pair<TimeInterval, List<BlockchainPublication>>>, VerifyElasticsearchDataByInterval.Request>() {

    override fun getRawObservable(request: Request): Observable<Result<Pair<TimeInterval, List<BlockchainPublication>>>> =
        getTimeIntervals.getRawSingle(GetTimeIntervals.Request(request.startAt, request.finishIn))
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMap { timeInterval ->
                Observable.fromIterable(elasticsearchConfiguration.indexPatterns)
                    .map { indexPattern -> Pair(indexPattern, timeInterval) }
            }.concatMapSingle { verifyElasticsearchData.getSingle(VerifyElasticsearchData.Request(it.first, it.second)) }

    data class Request(val startAt: Long, val finishIn: Long)
}