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

    fun insertAttestation(attestationDM: AttestationDM): Single<Int> =
        database.insert(
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
        ).andThen(database.getLastInsertedRowId())
            .subscribeOn(ioScheduler)
            .synchronize()

    //TODO insert several blockchainPublication at one time
    fun insertBlockchainPublication(blockchainPublicationDM: BlockchainPublicationDM): Completable =
        database.insert(
            "INSERT INTO ${TableBlockchainPublication.TABLE_NAME} " +
                    "(${TableBlockchainPublication.FK_ATTESTATION_ID}, " +
                    "${TableBlockchainPublication.BLOCKCHAIN}, " +
                    "${TableBlockchainPublication.BLOCK_ID}, " +
                    "${TableBlockchainPublication.TIMESTAMP}) " +
                    "VALUES (${blockchainPublicationDM.attestationId}, " +
                    "'${blockchainPublicationDM.blockchain}', " +
                    "'${blockchainPublicationDM.blockId}', " +
                    "${blockchainPublicationDM.timestamp})"
        ).subscribeOn(ioScheduler)

    fun getAttestations(): Single<List<AttestationDM>> =
        database.select("SELECT * FROM ${TableAttestation.TABLE_NAME}")
            .map {
                it.map { resultList ->
                    AttestationDM(
                        resultList[TableAttestation.ID] as Int,
                        resultList[TableAttestation.DATE_START] as Long,
                        resultList[TableAttestation.DATE_END] as Long,
                        resultList[TableAttestation.SOURCE] as String,
                        resultList[TableAttestation.DATE_TIMESTAMP] as Long,
                        resultList[TableAttestation.OTS_DATA] as ByteArray
                    )
                }
            }.subscribeOn(ioScheduler)

    fun getBlockchainValidations(): Single<List<BlockchainPublicationDM>> =
        database.select("SELECT * FROM ${TableBlockchainPublication.TABLE_NAME}")
            .map {
                it.map { resultList ->
                    BlockchainPublicationDM(
                        resultList[TableBlockchainPublication.ID] as Int,
                        resultList[TableBlockchainPublication.FK_ATTESTATION_ID] as Int,
                        resultList[TableBlockchainPublication.BLOCKCHAIN] as String,
                        resultList[TableBlockchainPublication.BLOCK_ID] as String,
                        resultList[TableBlockchainPublication.TIMESTAMP] as Long
                    )
                }
            }.subscribeOn(ioScheduler)
}