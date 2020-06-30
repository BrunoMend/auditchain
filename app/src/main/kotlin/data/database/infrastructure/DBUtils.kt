package data.database.infrastructure

import java.sql.*
import java.util.HashMap

fun Connection.newStatement(): Statement {
    val statement = this.createStatement()
    statement.queryTimeout = 10
    return statement
}

fun Connection.newPreparedStatement(
    query: String,
    bytesValue: ByteArray? = null
): PreparedStatement {
    val statement = this.prepareStatement(query)
    if (bytesValue != null) statement.setBytes(1, bytesValue)
    statement.queryTimeout = 10
    return statement
}

fun Statement.update(query: String): Statement {
    this.executeUpdate(query)
    return this
}

fun PreparedStatement.update(): PreparedStatement {
    this.execute()
    return this
}

@Throws(SQLException::class)
fun ResultSet.toList(): List<HashMap<String, Any>> {
    val metaData = this.metaData
    val columns = metaData.columnCount
    val list: MutableList<HashMap<String, Any>> = mutableListOf()
    while (this.next()) {
        val row = HashMap<String, Any>(columns)
        for (i in 1..columns) {
            row[metaData.getColumnName(i)] = this.getObject(i)
        }
        list.add(row)
    }
    return list
}

fun Connection?.safeClose() {
    if (this != null && !this.isClosed) this.close()
}

fun Statement?.safeClose() {
    if (this != null && !this.isClosed) this.close()
}

fun ResultSet?.safeClose() {
    if (this != null && !this.isClosed) this.close()
}