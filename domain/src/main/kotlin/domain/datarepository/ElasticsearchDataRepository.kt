package domain.datarepository

import domain.model.Attestation
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ElasticsearchDataRepository {
    fun getData(timeInterval: TimeInterval): Single<ByteArray>
    fun stampElasticsearchData(intervals: List<TimeInterval>): Observable<Attestation>
}