package domain.usecase

import domain.model.Attestation
import domain.model.Source
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ProcessAllElasticsearchStampExceptions @Inject constructor(
    private val getUnprocessedStampExceptions: GetUnprocessedStampExceptions,
    private val processElasticsearchStampException: ProcessElasticsearchStampException
) : ObservableUseCase<Result<Attestation>, Unit>() {

    override fun getRawObservable(request: Unit): Observable<Result<Attestation>> =
        getUnprocessedStampExceptions.getRawSingle(GetUnprocessedStampExceptions.Request(Source.ELASTICSEARCH))
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle {
                processElasticsearchStampException.getRawSingle(
                    ProcessElasticsearchStampException.Request(it)
                )
            }
}