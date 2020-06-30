package data.database

import data.SemaphoreDataSource
import data.database.infrastructure.Database
import data.database.infrastructure.TableStampException
import data.database.infrastructure.boolValue
import data.database.infrastructure.toBoolean
import data.database.model.StampExceptionDM
import domain.di.IOScheduler
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampExceptionDatabaseDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler,
    private val database: Database
) : SemaphoreDataSource() {

    fun insertStampException(stampExceptionDM: StampExceptionDM): Completable =
        database.upinsert(
            "INSERT INTO ${TableStampException.TABLE_NAME} " +
                    "( " +
                    "${TableStampException.DATE_START}, " +
                    "${TableStampException.DATE_END}, " +
                    "${TableStampException.SOURCE}, " +
                    "${TableStampException.DATE_EXCEPTION}, " +
                    "${TableStampException.EXCEPTION}, " +
                    "${TableStampException.PROCESSED} " +
                    ") " +
                    "VALUES ( " +
                    "${stampExceptionDM.dateStart}, " +
                    "${stampExceptionDM.dateEnd}, " +
                    "'${stampExceptionDM.source}', " +
                    "${stampExceptionDM.dateException}, " +
                    "'${stampExceptionDM.exception}', " +
                    "${stampExceptionDM.processed.boolValue} " +
                    ")"
        ).subscribeOn(ioScheduler)
            .synchronize()

    fun getUnprocessedStampExceptions(source: String): Single<List<StampExceptionDM>> =
        database.select(
            "SELECT * FROM ${TableStampException.TABLE_NAME} " +
                    "WHERE ${TableStampException.PROCESSED} = 0 " +
                    "AND ${TableStampException.SOURCE} = '${source}'"
        ).map { it.map { it.toStampExceptionDM() } }

    fun setAsProcessed(stampExceptionDM: StampExceptionDM): Completable =
        database.upinsert(
            "UPDATE ${TableStampException.TABLE_NAME} " +
                    "SET ${TableStampException.PROCESSED} = 1 " +
                    "WHERE ${TableStampException.DATE_START} = ${stampExceptionDM.dateStart} " +
                    "AND ${TableStampException.DATE_END} = ${stampExceptionDM.dateEnd} " +
                    "AND ${TableStampException.SOURCE} = '${stampExceptionDM.source}' " +
                    "AND ${TableStampException.DATE_EXCEPTION} = '${stampExceptionDM.dateException}'"
        )

    private fun HashMap<String, Any>.toStampExceptionDM() =
        StampExceptionDM(
            this[TableStampException.DATE_START] as Long,
            this[TableStampException.DATE_END] as Long,
            this[TableStampException.SOURCE] as String,
            this[TableStampException.EXCEPTION] as String,
            this[TableStampException.DATE_EXCEPTION] as Long,
            (this[TableStampException.PROCESSED] as Int).toBoolean()
        )
}