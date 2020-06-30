package domain.model

data class Attestation(
    val timeInterval: TimeInterval,
    val source: Source,
    val dateTimestamp: Long,
    val otsData: ByteArray,
    val blockchainPublications: List<BlockchainPublication> = listOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attestation

        if (timeInterval != other.timeInterval) return false
        if (source != other.source) return false
        if (dateTimestamp != other.dateTimestamp) return false
        if (!otsData.contentEquals(other.otsData)) return false
        if (blockchainPublications != other.blockchainPublications) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeInterval.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + dateTimestamp.hashCode()
        result = 31 * result + otsData.contentHashCode()
        result = 31 * result + blockchainPublications.hashCode()
        return result
    }
}