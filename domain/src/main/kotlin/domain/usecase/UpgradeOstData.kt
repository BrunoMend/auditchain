package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class UpgradeOstData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : CompletableUseCase<UpgradeOstData.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        timestampDataRepository.upgradeOstData(request.attestation)

    data class Request(val attestation: Attestation)
}