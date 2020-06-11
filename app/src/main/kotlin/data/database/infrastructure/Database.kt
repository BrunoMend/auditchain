package data.database.infrastructure

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.sql.*
import javax.inject.Inject

class Database @Inject constructor() {
    companion object {
        private var isInitialized: Boolean = false
    }

    init {
        if (!isInitialized) {
            val connection = newConnection()
            connection.newStatement()
                .update(TableAttestation.CREATE_TABLE)
                .update(TableBlockchainPublication.CREATE_TABLE)
                .safeClose()
            connection.safeClose()
            isInitialized = true
        }
    }

    fun newConnection(): Connection =
        DriverManager.getConnection("jdbc:sqlite:C:/ots/test5.db")

    fun insert(query: String, bytesValue: ByteArray? = null): Single<Int> {
        val sqlConnection = newConnection()
        return Completable.fromAction {
            sqlConnection
                .newPreparedStatement(query, bytesValue)
                .update()
                .safeClose()
        }.andThen(getLastInsertedRowId(sqlConnection))
            .doFinally { sqlConnection.safeClose() }
    }

    private fun getLastInsertedRowId(connection: Connection? = null): Single<Int> =
        Single.fromCallable {
            val sqlConnection = connection ?: newConnection()
            val getRowIdStatement = sqlConnection.createStatement()
            val resultSet = getRowIdStatement.executeQuery("SELECT last_insert_rowid()")
            resultSet.next()
            val rowId = resultSet.getInt(1)
            resultSet.safeClose()
            getRowIdStatement.safeClose()
            if (connection == null) sqlConnection.safeClose()
            rowId
        }
}