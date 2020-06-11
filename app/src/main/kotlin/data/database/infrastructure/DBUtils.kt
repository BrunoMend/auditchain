package data.database.infrastructure

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

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
    if(bytesValue != null) statement.setBytes(1, bytesValue)
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

fun Connection?.safeClose() {
    if (this != null && !this.isClosed) this.close()
}

fun Statement?.safeClose() {
    if (this != null && !this.isClosed) this.close()
}

fun ResultSet?.safeClose() {
    if (this != null && !this.isClosed) this.close()
}