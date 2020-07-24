package domain.usecase

import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class UpdateOtsData @Inject constructor(
    private val upgradeOstData: UpgradeOstData,
    private val saveUpdatedOtsData: SaveUpdatedOtsData
) : CompletableUseCase<UpdateOtsData.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        upgradeOstData.getCompletable(UpgradeOstData.Request(request.attestation))
            .andThen(saveUpdatedOtsData.getRawCompletable(SaveUpdatedOtsData.Request(request.attestation)))

    data class Request(val attestation: Attestation)
}