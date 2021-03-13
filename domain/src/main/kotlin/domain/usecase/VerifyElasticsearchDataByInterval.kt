package domain.usecase

import domain.model.AttestationVerifyResult
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class VerifyElasticsearchDataByInterval @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val verifyElasticsearchData: VerifyElasticsearchData
) : ObservableUseCase<Result<AttestationVerifyResult>, VerifyElasticsearchDataByInterval.Request>() {

    override fun getRawObservable(request: Request): Observable<Result<AttestationVerifyResult>> =
        getTimeIntervals.getRawSingle(GetTimeIntervals.Request(request.startAt, request.finishIn))
            .flatMapObservable { Observable.fromIterable(it) }
            .concatMapSingle { verifyElasticsearchData.getSingle(VerifyElasticsearchData.Request(it)) }

    data class Request(val startAt: Long, val finishIn: Long)
}