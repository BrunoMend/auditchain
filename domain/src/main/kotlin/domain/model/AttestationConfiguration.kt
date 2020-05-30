package domain.model

data class AttestationConfiguration(
    val frequency: Long,
    val delay: Long,
    val attestationFilePath: String
) {
    val timeIntervalList: List<Long>
        get() {
            val dayMinutes: Long = 1440
            val instantList = mutableListOf<Long>()
            for (temp in frequency until dayMinutes step frequency) {
                instantList.add(temp)
            }
            instantList.add(dayMinutes)
            return instantList
        }

    val frequencyMillis: Long
        get() = frequency * 60000

    val delayMillis: Long
        get() = delay * 1000
}