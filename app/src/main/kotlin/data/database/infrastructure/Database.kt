package data.database.infrastructure

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
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

    private fun newConnection(): Connection =
        DriverManager.getConnection("jdbc:sqlite:C:/ots/test5.db")

    fun getLastInsertedRowId(): Single<Int> =
        select("SELECT last_insert_rowid()")
            .map { it.first().toList().first().second as Int }

    fun insert(query: String, bytesValue: ByteArray? = null): Completable =
        Completable.fromAction {
            val sqlConnection = newConnection()
            sqlConnection
                .newPreparedStatement(query, bytesValue)
                .update()
                .safeClose()
            sqlConnection.safeClose()
        }

    fun select(query: String): Single<List<HashMap<String, Any>>> =
        Single.fromCallable {
            val sqlConnection = newConnection()
            val statement = sqlConnection.newStatement()
            val resultSet = statement.executeQuery(query)
            val resultList = resultSet.toList()
            resultSet.safeClose()
            statement.safeClose()
            sqlConnection.safeClose()
            resultList
        }
}