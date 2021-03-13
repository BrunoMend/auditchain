package data.database

import data.database.infrastructure.TableAttestation
import data.database.infrastructure.dao.AttestationDao
import data.database.model.AttestationDM
import data.database.model.SourceDM
import data.mappers.toDatabaseModel
import data.mappers.toDomainModel
import domain.di.IOScheduler
import domain.exception.NoAttestationException
import domain.utility.synchronize
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import javax.inject.Inject

class AttestationDatabaseDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler
) : BaseDatabaseDataSource() {

    fun insertAttestation(attestationDM: AttestationDM): Completable =
        Completable.fromAction {
            transaction {
                AttestationDao.new {
                    dateStart = attestationDM.dateStart
                    dateEnd = attestationDM.dateEnd
                    dataSource = attestationDM.source
                    dateTimestamp = attestationDM.dateTimestamp
                    dataSignature = ExposedBlob(attestationDM.dataSignature)
                    otsData = ExposedBlob(attestationDM.otsData)
                    dateOtsComplete = attestationDM.dateOtsComplete
                }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun updateOtsData(attestationDM: AttestationDM): Completable =
        Completable.fromAction {
            transaction {
                (attestationDM.id?.let {
                    AttestationDao.findById(it)
                } ?: getAttestationDao(
                    attestationDM.dateStart,
                    attestationDM.dateEnd,
                    attestationDM.source
                )).apply {
                    otsData = ExposedBlob(attestationDM.otsData)
                    dateOtsComplete = attestationDM.dateOtsComplete
                }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun getIncompleteOtsAttestations(): Single<List<AttestationDM>> =
        Single.fromCallable {
            transaction {
                AttestationDao.find { TableAttestation.dateOtsComplete eq null }
                    .map { it.toDatabaseModel() }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun getAttestation(
        dateStart: Long,
        dateEnd: Long,
        source: SourceDM
    ): Single<AttestationDM> =
        Single.fromCallable {
            transaction {
                getAttestationDao(dateStart, dateEnd, source).toDatabaseModel()
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun getLastAttestation(source: SourceDM): Single<AttestationDM> =
        Single.fromCallable {
            transaction {
                AttestationDao
                    .find { TableAttestation.dataSource eq source }
                    .maxByOrNull { it.dateEnd }
                    ?.toDatabaseModel() ?: throw NoAttestationException(source.toDomainModel(), null)
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    // must be called within a transaction
    private fun getAttestationDao(
        dateStart: Long,
        dateEnd: Long,
        source: SourceDM
    ): AttestationDao =
        AttestationDao.find {
            (TableAttestation.dateStart eq dateStart) and
                    (TableAttestation.dateEnd eq dateEnd) and
                    (TableAttestation.dataSource eq source)
        }.single()
}