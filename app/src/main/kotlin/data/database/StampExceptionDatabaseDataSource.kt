package data.database

import data.database.infrastructure.dao.StampExceptionDao
import data.database.model.StampExceptionDM
import domain.di.IOScheduler
import domain.utility.synchronize
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.jetbrains.exposed.sql.transactions.transaction
import javax.inject.Inject

/**
 * Exceptions are printed in the log file and also saved in the database
 */
class StampExceptionDatabaseDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler
) : BaseDatabaseDataSource() {

    fun insertStampException(stampExceptionDM: StampExceptionDM): Completable =
        Completable.fromAction {
            transaction {
                StampExceptionDao.new {
                    dateStart = stampExceptionDM.dateStart
                    dateEnd = stampExceptionDM.dateEnd
                    dataSource = stampExceptionDM.source
                    exception = stampExceptionDM.exception
                    dateException = stampExceptionDM.dateException
                }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

}