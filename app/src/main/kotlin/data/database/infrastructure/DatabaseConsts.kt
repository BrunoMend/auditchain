package data.database.infrastructure

const val DATABASE_URL = "jdbc:sqlite:C:/ots/test14.db"

object EnumSource {
    const val ELASTICSEARCH = "ES"
    const val POSTEGRES = "PSql"
}

object TableAttestation {
    const val TABLE_NAME = "attestation"

    const val DATE_START = "date_start"
    const val DATE_END = "date_end"
    const val SOURCE = "source"
    const val DATE_TIMESTAMP = "date_timestamp"
    const val OTS_DATA = "ots_data"
    const val IS_OTS_UPDATED = "is_ots_updated"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$DATE_START LONG NOT NULL, " +
                "$DATE_END LONG NOT NULL, " +
                "$SOURCE VARCHAR(10) CHECK( $SOURCE IN ('${EnumSource.ELASTICSEARCH}', '${EnumSource.POSTEGRES}') ) NOT NULL, " +
                "$DATE_TIMESTAMP LONG NOT NULL, " +
                "$OTS_DATA BLOB NOT NULL, " +
                "$IS_OTS_UPDATED BOOL NOT NULL, " +
                "UNIQUE ($DATE_START, $DATE_END, $SOURCE))"
}

object TableStampException {
    const val TABLE_NAME = "stamp_exception"

    const val DATE_START = "date_start"
    const val DATE_END = "date_end"
    const val SOURCE = "source"
    const val EXCEPTION = "exception"
    const val DATE_EXCEPTION = "date_exception"
    const val PROCESSED = "processed"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$DATE_START LONG NOT NULL, " +
                "$DATE_END LONG NOT NULL, " +
                "$SOURCE VARCHAR(15) CHECK( $SOURCE IN ('${EnumSource.ELASTICSEARCH}', '${EnumSource.POSTEGRES}') ) NOT NULL, " +
                "$EXCEPTION VARCHAR(150) NOT NULL, " +
                "$DATE_EXCEPTION LONG NOT NULL, " +
                "$PROCESSED BOOL NOT NULL, " +
                "UNIQUE ($DATE_START, $DATE_END, $SOURCE, $DATE_EXCEPTION))"
}