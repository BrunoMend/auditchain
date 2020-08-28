package data.database

import data.database.infrastructure.TableAttestation
import data.database.infrastructure.dao.AttestationDao
import data.database.infrastructure.toMapString
import data.database.model.AttestationDM
import data.database.model.SourceDM
import data.mappers.toDatabaseModel
import domain.di.IOScheduler
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
                    sourceParams = attestationDM.sourceParams?.toMapString()
                    dateTimestamp = attestationDM.dateTimestamp
                    dataSignature = ExposedBlob(attestationDM.dataSignature)
                    otsData = ExposedBlob(attestationDM.otsData)
                    isOtsComplete = attestationDM.isOtsComplete
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
                    attestationDM.source,
                    attestationDM.sourceParams
                )).apply {
                    otsData = ExposedBlob(attestationDM.otsData)
                    isOtsComplete = attestationDM.isOtsComplete
                }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun getIncompleteOtsAttestations(): Single<List<AttestationDM>> =
        Single.fromCallable {
            transaction {
                AttestationDao.find { TableAttestation.isOtsComplete eq false }
                    .map { it.toDatabaseModel() }
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun getAttestation(
        dateStart: Long,
        dateEnd: Long,
        source: SourceDM,
        sourceParams: Map<String, String>?
    ): Single<AttestationDM> =
        Single.fromCallable {
            transaction {
                getAttestationDao(dateStart, dateEnd, source, sourceParams).toDatabaseModel()
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    fun getLastAttestation(source: SourceDM): Single<AttestationDM?> =
        Single.fromCallable {
            transaction {
                AttestationDao
                    .find { TableAttestation.dataSource eq source }
                    .maxBy { it.dateEnd }
                    ?.toDatabaseModel()
            }
        }.synchronize(databaseSemaphore)
            .subscribeOn(ioScheduler)

    // must be called within a transaction
    private fun getAttestationDao(
        dateStart: Long,
        dateEnd: Long,
        source: SourceDM,
        sourceParams: Map<String, String>?
    ): AttestationDao =
        AttestationDao.find {
            (TableAttestation.dateStart eq dateStart) and
                    (TableAttestation.dateEnd eq dateEnd) and
                    (TableAttestation.dataSource eq source) and
                    (TableAttestation.sourceParams eq sourceParams?.toMapString())
        }.single()
}