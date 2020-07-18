package domain.usecase

import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class UpdateOtsData @Inject constructor(
    private val upgradeOstData: UpgradeOstData,
    private val verifyIsOtsCompletelyUpdated: VerifyIsOtsCompletelyUpdated,
    private val saveUpdatedOtsData: SaveUpdatedOtsData
) {
    fun getCompletable(attestation: Attestation): Completable =
        upgradeOstData.getRawSingle(UpgradeOstData.Request(attestation.otsData))
            .flatMapCompletable { updatedOtsData ->
                if (!attestation.otsData.contentEquals(updatedOtsData)) {
                    attestation.otsData = updatedOtsData
                    verifyIsOtsCompletelyUpdated
                        .getRawSingle(VerifyIsOtsCompletelyUpdated.Request(updatedOtsData))
                        .flatMapCompletable {
                            attestation.isOtsUpdated = it
                            saveUpdatedOtsData
                                .getRawCompletable(SaveUpdatedOtsData.Request(attestation))
                        }
                } else Completable.complete()
            }
}