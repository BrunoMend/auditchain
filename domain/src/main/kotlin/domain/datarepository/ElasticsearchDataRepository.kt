package domain.datarepository

import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single

interface ElasticsearchDataRepository {
    fun getElasticsearchData(timeInterval: TimeInterval): Single<ByteArray>
}