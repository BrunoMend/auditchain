package domain.datarepository

import io.reactivex.rxjava3.core.Single

interface ElasticSearchDataRepository {
    fun getData(query: String): Single<ByteArray>
}