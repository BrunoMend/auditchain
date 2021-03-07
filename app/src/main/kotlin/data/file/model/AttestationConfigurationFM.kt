package data.file.model

data class AttestationConfigurationFM (
    val frequency: Long,
    val delay: Long,
    val signingKeyFilePath: String,
    val verifyKeyFilePath: String
)