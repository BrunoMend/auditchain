package domain.usecase

import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class UpdateAttestationOtsData @Inject constructor(
    private val upgradeOstData: UpgradeOstData,
    private val saveUpdatedOtsData: SaveUpdatedOtsData
) : CompletableUseCase<UpdateAttestationOtsData.Request>() {

    override fun getRawCompletable(request: Request): Completable =
        upgradeOstData.getSingle(UpgradeOstData.Request(request.attestation))
            .flatMapCompletable { saveUpdatedOtsData.getRawCompletable(SaveUpdatedOtsData.Request(it)) }

    data class Request(val attestation: Attestation)
}