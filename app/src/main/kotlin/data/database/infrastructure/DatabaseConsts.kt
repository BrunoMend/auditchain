package data.database.infrastructure

const val DATABASE_URL = "jdbc:sqlite:C:/ots/test11.db"

object TableAttestation {
    const val TABLE_NAME = "attestation"

    const val DATE_START = "date_start"
    const val DATE_END = "date_end"
    const val SOURCE = "source"
    const val DATE_TIMESTAMP = "date_timestamp"
    const val OTS_DATA = "ots_data"
    const val IS_OTS_UPDATED = "is_ots_updated"
    const val HAS_NO_DATA = "has_no_data"

    const val SOURCE_ELASTICSEARCH = "ES"
    const val SOURCE_POSTEGRES = "PSql"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$DATE_START LONG NOT NULL, " +
                "$DATE_END LONG NOT NULL, " +
                "$SOURCE VARCHAR(10) CHECK( $SOURCE IN ('$SOURCE_ELASTICSEARCH', '$SOURCE_POSTEGRES') ) NOT NULL, " +
                "$DATE_TIMESTAMP LONG, " +
                "$OTS_DATA BLOB, " +
                "$IS_OTS_UPDATED BOOL NOT NULL, " +
                "$HAS_NO_DATA BOOL NOT NULL, " +
                "UNIQUE ($DATE_START, $DATE_END, $SOURCE))"
}