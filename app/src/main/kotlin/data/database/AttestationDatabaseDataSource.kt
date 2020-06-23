package data.database

import data.SemaphoreDataSource
import data.database.infrastructure.Database
import data.database.infrastructure.TableAttestation
import data.database.model.AttestationDM
import data.mappers.toDatabaseModel
import domain.di.IOScheduler
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AttestationDatabaseDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler,
    private val database: Database
) : SemaphoreDataSource() {

    fun insertAttestation(attestationDM: AttestationDM): Completable =
        database.upinsert(
            "INSERT INTO ${TableAttestation.TABLE_NAME} " +
                    "(${TableAttestation.DATE_START}, " +
                    "${TableAttestation.DATE_END}, " +
                    "${TableAttestation.SOURCE}, " +
                    "${TableAttestation.OTS_DATA}, " +
                    "${TableAttestation.DATE_TIMESTAMP}) " +
                    "VALUES (${attestationDM.dateStart}, " +
                    "${attestationDM.dateEnd}, " +
                    "'${attestationDM.source}', " +
                    "?, " +
                    "${attestationDM.dateTimestamp})",
            attestationDM.otsData
        ).subscribeOn(ioScheduler)
            .synchronize()

    fun updateOtsData(attestationDM: AttestationDM): Completable =
        database.upinsert(
            "UPDATE ${TableAttestation.TABLE_NAME} " +
                    "SET ${TableAttestation.OTS_DATA} = ?, " +
                    "${TableAttestation.IS_OTS_UPDATED} = ${attestationDM.isOtsUpdated} " +
                    "WHERE ${TableAttestation.DATE_START} = ${attestationDM.dateStart} " +
                    "AND ${TableAttestation.DATE_END} = ${attestationDM.dateEnd} " +
                    "AND ${TableAttestation.SOURCE} = '${attestationDM.source}'",
            attestationDM.otsData
        ).subscribeOn(ioScheduler)
            .synchronize()

    fun getAllAttestations(): Single<List<AttestationDM>> =
        database.select("SELECT * FROM ${TableAttestation.TABLE_NAME}")
            .map { it.map { it.toAttestationDM() } }
            .subscribeOn(ioScheduler)

    fun getAttestation(timeInterval: TimeInterval, source: Source): Single<AttestationDM> =
        database.select(
            "SELECT * FROM ${TableAttestation.TABLE_NAME} " +
                    "WHERE ${TableAttestation.DATE_START} = ${timeInterval.startAt} " +
                    "AND ${TableAttestation.DATE_END} = ${timeInterval.finishIn} " +
                    "AND ${TableAttestation.SOURCE} = '${source.toDatabaseModel()}'"
        ).map { it.first().toAttestationDM() }

    private fun HashMap<String, Any>.toAttestationDM() =
        AttestationDM(
            this[TableAttestation.DATE_START] as Long,
            this[TableAttestation.DATE_END] as Long,
            this[TableAttestation.SOURCE] as String,
            this[TableAttestation.DATE_TIMESTAMP] as Long,
            this[TableAttestation.OTS_DATA] as ByteArray,
            this[TableAttestation.IS_OTS_UPDATED] as Boolean
        )
}