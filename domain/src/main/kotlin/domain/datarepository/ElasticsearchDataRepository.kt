package domain.datarepository

import domain.model.Attestation
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface ElasticsearchDataRepository {
    fun verifyElasticsearchData(intervals: List<TimeInterval>): Completable
    fun stampElasticsearchData(intervals: List<TimeInterval>): Observable<Attestation>
}