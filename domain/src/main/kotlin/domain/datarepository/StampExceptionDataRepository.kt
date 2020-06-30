package domain.datarepository

import domain.model.Source
import domain.model.StampException
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StampExceptionDataRepository {
    fun saveStampException(stampException: StampException): Completable
    fun getUnprocessedStampExceptions(source: Source): Single<List<StampException>>
    fun setStampExceptionAsProcessed(stampException: StampException): Completable
}