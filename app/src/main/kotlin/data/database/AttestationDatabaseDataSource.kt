package data.database

import data.SemaphoreDataSource
import data.database.infrastructure.TableAttestation
import data.database.infrastructure.TableBlockchainPublication
import data.database.model.AttestationDM
import data.database.model.BlockchainPublicationDM
import domain.di.IOScheduler
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.sql.Connection
import java.sql.ResultSet
import javax.inject.Inject

class AttestationDatabaseDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler,
    private val sqlConnection: Connection
): SemaphoreDataSource() {
    fun insertAttestation(attestationDM: AttestationDM): Single<Int> =
        Single.fromCallable {
            val insertStatement = sqlConnection.prepareStatement(
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
                        "${attestationDM.dateTimestamp})"
            )
            insertStatement.setBytes(1, attestationDM.otsData)
            insertStatement.execute()
            insertStatement.close()
            val getRowIdStatement = sqlConnection.createStatement()
            val resultSet = getRowIdStatement.executeQuery("SELECT last_insert_rowid()")
            resultSet.next()
            val rowId = resultSet.getInt(1)
            resultSet.close()
            getRowIdStatement.close()
            rowId
        }.subscribeOn(ioScheduler).synchronize()

    //TODO insert several blockchainPublication at one time
    fun insertBlockchainPublication(blockchainPublicationDM: BlockchainPublicationDM): Completable =
        Completable.fromAction {
            val statement = sqlConnection.prepareStatement(
                "INSERT INTO ${TableBlockchainPublication.TABLE_NAME} " +
                        "(${TableBlockchainPublication.FK_ATTESTATION_ID}, " +
                        "${TableBlockchainPublication.BLOCKCHAIN}, " +
                        "${TableBlockchainPublication.BLOCK_ID}, " +
                        "${TableBlockchainPublication.TIMESTAMP}) " +
                        "VALUES (${blockchainPublicationDM.attestationId}, " +
                        "'${blockchainPublicationDM.blockchain}', " +
                        "'${blockchainPublicationDM.blockId}', " +
                        "${blockchainPublicationDM.timestamp})"
            )
            statement.execute()
        }.subscribeOn(ioScheduler)

    fun getAttestations(): Single<List<AttestationDM>> =
        Single.fromCallable {
            val result = mutableListOf<AttestationDM>()
            val resultSet: ResultSet =
                sqlConnection.createStatement().executeQuery("SELECT * FROM ${TableAttestation.TABLE_NAME}")
            while (resultSet.next()) {
                result.add(
                    AttestationDM(
                        resultSet.getInt(TableAttestation.ID),
                        resultSet.getLong(TableAttestation.DATE_START),
                        resultSet.getLong(TableAttestation.DATE_END),
                        resultSet.getString(TableAttestation.SOURCE),
                        resultSet.getBytes(TableAttestation.OTS_DATA),
                        resultSet.getLong(TableAttestation.DATE_TIMESTAMP)
                    )
                )
            }
            result as List<AttestationDM>
        }.subscribeOn(ioScheduler)

    fun getBlockchainValidation(): Single<List<BlockchainPublicationDM>> =
        Single.fromCallable {
            val result = mutableListOf<BlockchainPublicationDM>()
            val resultSet: ResultSet =
                sqlConnection.createStatement().executeQuery("SELECT * FROM ${TableBlockchainPublication.TABLE_NAME}")
            while (resultSet.next()) {
                result.add(
                    BlockchainPublicationDM(
                        resultSet.getInt(TableBlockchainPublication.ID),
                        resultSet.getInt(TableBlockchainPublication.FK_ATTESTATION_ID),
                        resultSet.getString(TableBlockchainPublication.BLOCKCHAIN),
                        resultSet.getString(TableBlockchainPublication.BLOCK_ID),
                        resultSet.getLong(TableBlockchainPublication.TIMESTAMP)
                    )
                )
            }
            result as List<BlockchainPublicationDM>
        }.subscribeOn(ioScheduler)
}