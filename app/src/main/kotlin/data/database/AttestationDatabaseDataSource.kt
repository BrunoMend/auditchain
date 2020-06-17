package data.database

import data.SemaphoreDataSource
import data.database.infrastructure.Database
import data.database.infrastructure.TableAttestation
import data.database.infrastructure.TableBlockchainPublication
import data.database.model.AttestationDM
import data.database.model.BlockchainPublicationDM
import domain.di.IOScheduler
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AttestationDatabaseDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler,
    private val database: Database
) : SemaphoreDataSource() {

    //TODO
    // get attestation id from source and time interval passed
    // if have no data, insert attestation
    // else upinsert blockchain publications
    fun insertAttestation(attestationDM: AttestationDM): Completable =
        database.insert(
            "INSERT OR IGNORE INTO ${TableAttestation.TABLE_NAME} " +
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
        ).andThen(database.getLastInsertedRowId())
            .flatMapCompletable { attestationId ->
                attestationDM.blockchainPublications?.let {
                    insertBlockchainPublications(it, attestationId)
                } ?: Completable.complete()
            }
            .subscribeOn(ioScheduler)
            .synchronize()

    private fun insertBlockchainPublications(
        blockchainPublications: List<BlockchainPublicationDM>,
        attestationId: Int
    ): Completable =
        database.insert(
            "INSERT OR IGNORE INTO ${TableBlockchainPublication.TABLE_NAME} " +
                    "(${TableBlockchainPublication.FK_ATTESTATION_ID}, " +
                    "${TableBlockchainPublication.BLOCKCHAIN}, " +
                    "${TableBlockchainPublication.BLOCK_ID}, " +
                    "${TableBlockchainPublication.DATE_PUBLICATION}) " +
                    "VALUES " +
                    blockchainPublications.forEachIndexed { index, blockchainPublication ->
                        "($attestationId, " +
                                "'${blockchainPublication.blockchain}', " +
                                "'${blockchainPublication.blockId}', " +
                                "${blockchainPublication.datePublication})" +
                                if (index != blockchainPublications.lastIndex) ", " else ""
                    }
        ).subscribeOn(ioScheduler)

    fun getAttestations(): Single<List<AttestationDM>> =
        database.select("SELECT * FROM ${TableAttestation.TABLE_NAME}")
            .map {
                it.map { resultList ->
                    AttestationDM(
                        resultList[TableAttestation.DATE_START] as Long,
                        resultList[TableAttestation.DATE_END] as Long,
                        resultList[TableAttestation.SOURCE] as String,
                        resultList[TableAttestation.DATE_TIMESTAMP] as Long,
                        resultList[TableAttestation.OTS_DATA] as ByteArray,
                        resultList[TableAttestation.ID] as Int
                    )
                }
            }.subscribeOn(ioScheduler)

    fun getBlockchainValidations(): Single<List<BlockchainPublicationDM>> =
        database.select("SELECT * FROM ${TableBlockchainPublication.TABLE_NAME}")
            .map {
                it.map { resultList ->
                    BlockchainPublicationDM(
                        resultList[TableBlockchainPublication.BLOCKCHAIN] as String,
                        resultList[TableBlockchainPublication.BLOCK_ID] as String,
                        resultList[TableBlockchainPublication.DATE_PUBLICATION] as Long,
                        resultList[TableBlockchainPublication.ID] as Int,
                        resultList[TableBlockchainPublication.FK_ATTESTATION_ID] as Int
                    )
                }
            }.subscribeOn(ioScheduler)
}