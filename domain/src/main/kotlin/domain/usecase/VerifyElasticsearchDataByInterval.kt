package domain.usecase

import domain.model.AttestationVerifyResult
import domain.model.ElasticsearchConfiguration
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class VerifyElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val verifyElasticsearchData: VerifyElasticsearchData,
    private val elasticsearchConfiguration: ElasticsearchConfiguration
) : ObservableUseCase<Result<AttestationVerifyResult>, VerifyElasticsearchDataByInterval.Request>() {

    override fun getRawObservable(request: Request): Observable<Result<AttestationVerifyResult>> =
        getTimeIntervals.getRawSingle(GetTimeIntervals.Request(request.startAt, request.finishIn))
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMap { timeInterval ->
                Observable.fromIterable(elasticsearchConfiguration.indexPatterns)
                    .map { indexPattern -> Pair(indexPattern, timeInterval) }
            }.concatMapSingle { verifyElasticsearchData.getSingle(VerifyElasticsearchData.Request(it.first, it.second)) }

    data class Request(val startAt: Long, val finishIn: Long)
}