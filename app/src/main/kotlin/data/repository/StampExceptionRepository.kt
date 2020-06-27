package data.repository

import data.database.StampExceptionDatabaseDataSource
import data.mappers.toDatabaseModel
import data.mappers.toDomainModel
import domain.datarepository.StampExceptionDataRepository
import domain.model.Source
import domain.model.StampException
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampExceptionRepository @Inject constructor(
    private val stampExceptionDatabaseDataSource: StampExceptionDatabaseDataSource
) : StampExceptionDataRepository {

    override fun saveStampException(stampException: StampException): Completable =
        stampExceptionDatabaseDataSource.insertStampException(stampException.toDatabaseModel())

    override fun getUnprocessedStampExceptions(source: Source): Single<List<StampException>> =
        stampExceptionDatabaseDataSource.getUnprocessedStampExceptions(source.toDatabaseModel())
            .map { it.map { it.toDomainModel() } }

    override fun setAsProcessed(stampException: StampException): Completable =
        stampExceptionDatabaseDataSource.setAsProcessed(stampException.toDatabaseModel())
}