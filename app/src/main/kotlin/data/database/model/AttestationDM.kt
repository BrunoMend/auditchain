package data.database.model

data class AttestationDM(
    val dateStart: Long,
    val dateEnd: Long,
    val source: String,
    val dateTimestamp: Long,
    val otsData: ByteArray,
    val id: Int? = null,
    val blockchainPublications: List<BlockchainPublicationDM>? = null
)