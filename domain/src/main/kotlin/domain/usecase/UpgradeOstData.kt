package domain.usecase

import domain.datarepository.TimestampDataRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpgradeOstData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) {
    fun getSingle(otsData: ByteArray): Single<ByteArray> =
        timestampDataRepository.upgradeOstData(otsData)
}