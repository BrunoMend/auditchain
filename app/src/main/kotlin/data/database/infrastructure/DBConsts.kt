package data.database.infrastructure

object TableAttestation {
    const val TABLE_NAME = "attestation"

    const val ID = "id"
    const val DATE_START = "date_start"
    const val DATE_END = "date_end"
    const val SOURCE = "source"
    const val OTS_DATA = "ots_data"
    const val DATE_TIMESTAMP = "date_timestamp"

    const val SOURCE_ELASTICSEARCH = "ES"
    const val SOURCE_POSTEGRES = "PSql"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID INTEGER NOT NULL, " +
                "$DATE_START LONG NOT NULL, " +
                "$DATE_END LONG NOT NULL, " +
                "$SOURCE VARCHAR(10) CHECK( $SOURCE IN ('$SOURCE_ELASTICSEARCH', '$SOURCE_POSTEGRES') ) NOT NULL, " +
                "$OTS_DATA BLOB NOT NULL, " +
                "$DATE_TIMESTAMP LONG NOT NULL, " +
                "PRIMARY KEY ($ID));"
}

object TableBlockchainPublication {
    const val TABLE_NAME = "blockchain_publication"

    const val ID = "id"
    const val FK_ATTESTATION_ID = "attestation_id"
    const val BLOCKCHAIN = "blockchain"
    const val BLOCK_ID = "block_id"
    const val TIMESTAMP = "timestamp"

    const val BLOCKCHAIN_BITCOIN = "BTC"
    const val BLOCKCHAIN_ETHEREUM = "ETH"
    const val BLOCKCHAIN_LITECOIN = "LTC"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$ID INTEGER NOT NULL, " +
                "$FK_ATTESTATION_ID INTEGER NOT NULL, " +
                "$BLOCKCHAIN VARCHAR(10) CHECK( $BLOCKCHAIN IN ('$BLOCKCHAIN_BITCOIN', '$BLOCKCHAIN_ETHEREUM', '$BLOCKCHAIN_LITECOIN') ), " +
                "$BLOCK_ID TEXT NOT NULL, " +
                "$TIMESTAMP LONG NOT NULL, " +
                "PRIMARY KEY (${ID}) " +
                "FOREIGN KEY ($FK_ATTESTATION_ID) " +
                "    REFERENCES ${TableAttestation.TABLE_NAME} (${TableAttestation.ID}) " +
                "      ON DELETE NO ACTION" +
                "      ON UPDATE NO ACTION);"
}