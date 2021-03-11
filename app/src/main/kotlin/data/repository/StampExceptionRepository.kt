package data.repository

import data.database.StampExceptionDatabaseDataSource
import data.mappers.toDatabaseModel
import domain.datarepository.StampExceptionDataRepository
import domain.model.StampException
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class StampExceptionRepository @Inject constructor(
    private val stampExceptionDatabaseDataSource: StampExceptionDatabaseDataSource
) : StampExceptionDataRepository {

    override fun saveStampException(stampException: StampException): Completable =
        stampExceptionDatabaseDataSource.insertStampException(stampException.toDatabaseModel())

}