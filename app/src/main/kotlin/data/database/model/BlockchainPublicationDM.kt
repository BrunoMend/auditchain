package data.database.model

data class BlockchainPublicationDM(
    val blockchain: String,
    val blockId: String,
    val datePublication: Long,
    val attestationId: Int? = null,
    val id: Int? = null
)