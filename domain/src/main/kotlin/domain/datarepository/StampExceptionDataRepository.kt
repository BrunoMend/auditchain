package domain.datarepository

import domain.model.StampException
import io.reactivex.rxjava3.core.Completable

interface StampExceptionDataRepository {
    fun saveStampException(stampException: StampException): Completable
}