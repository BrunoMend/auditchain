package data.database.infrastructure

import data.database.model.SourceDM
import org.jetbrains.exposed.dao.id.LongIdTable

const val DATABASE_URL = "jdbc:sqlite://C:/ots/data.db" //TODO jdbc:sqlite:/data.db - getting access denied
const val DATABASE_DRIVER = "org.sqlite.JDBC"

object TableAttestation: LongIdTable() {
    val dateStart = long("date_start")
    val dateEnd = long("date_end")
    val dataSource = enumerationByName("data_source", 30, SourceDM::class)
    val dateTimestamp = long("date_timestamp")
    val otsData = blob("ots_data")
    val isOtsComplete = bool("is_ots_complete")
    init {
        index(true, dateStart, dateEnd, dataSource)
    }
}

object TableStampException: LongIdTable() {
    val dateStart = long("date_start")
    val dateEnd = long("date_end")
    val dataSource = enumerationByName("data_source", 30, SourceDM::class)
    val exception = varchar("exception", 150)
    val dateException = long("date_exception")
    val processed = bool("processed")
}