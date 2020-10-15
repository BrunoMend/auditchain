package domain.model

data class AttestationVerifyResult(
    val attestation: Attestation,
    val blockchainPublications: List<BlockchainPublication>
)