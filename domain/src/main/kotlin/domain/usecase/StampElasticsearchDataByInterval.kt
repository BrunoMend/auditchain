package domain.usecase

import domain.model.Attestation
import domain.model.ElasticsearchConfiguration
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class StampElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val stampElasticsearchData: StampElasticsearchData,
    private val elasticsearchConfiguration: ElasticsearchConfiguration
) : ObservableUseCase<Result<Attestation>, StampElasticsearchDataByInterval.Request>() {

    override fun getRawObservable(request: Request): Observable<Result<Attestation>> =
        getTimeIntervals.getRawSingle(GetTimeIntervals.Request(request.startAt, request.finishIn))
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMap { timeInterval ->
                Observable.fromIterable(elasticsearchConfiguration.indexPatterns)
                    .map { indexPattern -> Pair(indexPattern, timeInterval) }
            }.concatMapSingle { stampElasticsearchData.getRawSingle(StampElasticsearchData.Request(it.first, it.second)) }

    data class Request(val startAt: Long, val finishIn: Long)
}