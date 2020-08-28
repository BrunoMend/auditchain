package data.database

import data.database.infrastructure.TableStampException
import data.database.infrastructure.dao.StampExceptionDao
import data.database.infrastructure.toMapString
import data.database.model.SourceDM
import data.database.model.StampExceptionDM
import data.mappers.toDatabaseModel
import domain.di.IOScheduler
import domain.utility.synchronize
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import javax.inject.Inject

class StampExceptionDatabaseDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler
) : BaseDatabaseDataSource() {

    fun insertStampException(stampExceptionDM: StampExceptionDM): Completable =
        Completable.fromAction {
            transaction {
                StampExceptionDao.find {
                    (TableStampException.dataSource eq stampExceptionDM.source) and
                            (TableStampException.dateStart eq stampExceptionDM.dateStart) and
                            (TableStampException.dateEnd eq stampExceptionDM.dateEnd) and
                            (TableStampException.processed eq false)
                }.forEach {
                    it.processed = true
                }
                StampExceptionDao.new {
                    dateStart = stampExceptionDM.dateStart
                    dateEnd = stampExceptionDM.dateEnd
                    dataSource = stampExceptionDM.source
                    sourceParams = stampExceptionDM.sourceParams?.toMapString()
                    exception = stampExceptionDM.exception
                    dateException = stampExceptionDM.dateException
                    processed = stampExceptionDM.processed
                }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun getUnprocessedStampExceptions(source: SourceDM): Single<List<StampExceptionDM>> =
        Single.fromCallable {
            transaction {
                StampExceptionDao.find {
                    (TableStampException.dataSource eq source) and
                            (TableStampException.processed eq false)
                }.map { it.toDatabaseModel() }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun setAsProcessed(stampExceptionDM: StampExceptionDM): Completable =
        Completable.fromAction {
            transaction {
                StampExceptionDao.findById(stampExceptionDM.id ?: throw NullPointerException())?.apply {
                    processed = true
                } ?: throw NullPointerException()
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)
}