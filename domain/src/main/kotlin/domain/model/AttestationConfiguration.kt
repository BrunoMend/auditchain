package domain.model

data class AttestationConfiguration(
    val frequency: Long,
    val delay: Long,
    val tryAgainTimeout: Long,
    val maxTimeInterval: Long,
    val attestationFilePath: String
) {
    val frequencyMillis: Long
        get() = frequency * 60000

    val tryAgainTimeoutMillis: Long
        get() =  tryAgainTimeout * 60000

    val delayMillis: Long
        get() = delay * 1000

    val maxTimeIntervalMillis: Long
        get() = maxTimeInterval * 3600000
}