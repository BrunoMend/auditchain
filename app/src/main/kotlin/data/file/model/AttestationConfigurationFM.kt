package data.file.model

data class AttestationConfigurationFM (
    val frequency: Long,
    val delay: Long,
    val tryAgainTimeout: Long,
    val maxTimeInterval: Long,
    val privateKeyFilePath: String,
    val publicKeyFilePath: String
)