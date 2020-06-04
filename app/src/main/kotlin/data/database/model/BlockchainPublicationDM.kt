package data.database.model

data class BlockchainPublicationDM(
    val id: Int,
    val attestationId: Int,
    val blockchain: String,
    val blockId: String
)