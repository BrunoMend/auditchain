package domain.model

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val dateTimestamp: Long,
    val otsData: ByteArray,
    val blockchainPublications: List<BlockchainPublication> = listOf()
)