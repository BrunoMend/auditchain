package domain.usecase

import domain.datarepository.TimestampDataRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class VerifyIsOtsCompletelyUpdated @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) {
    fun getSingle(otsData: ByteArray): Single<Boolean> =
        timestampDataRepository.verifyIsOtsCompletelyUpdated(otsData)
}