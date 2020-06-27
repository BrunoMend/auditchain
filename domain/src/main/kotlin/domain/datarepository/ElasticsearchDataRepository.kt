package domain.datarepository

import domain.model.Attestation
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ElasticsearchDataRepository {
    fun verifyElasticsearchData(intervals: List<TimeInterval>): Completable
    fun stampElasticsearchData(timeInterval: TimeInterval): Single<Attestation>
}