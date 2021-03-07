package domain.model

import domain.cryptography.SigningKey
import domain.cryptography.VerifyKey

data class AttestationConfiguration(
    val frequency: Long,
    val delay: Long,
    val maxTimeInterval: Long,
    val signingKey: SigningKey,
    val verifyKey: VerifyKey
) {
    val frequencyMillis: Long
        get() = frequency * 60000

    val delayMillis: Long
        get() = delay * 1000

    val maxTimeIntervalMillis: Long
        get() = maxTimeInterval * 3600000
}