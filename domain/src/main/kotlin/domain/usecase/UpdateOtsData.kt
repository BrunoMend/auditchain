package domain.usecase

import domain.di.IOScheduler
import domain.model.Attestation
import domain.utility.Logger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class UpdateOtsData @Inject constructor(
    private val upgradeOstData: UpgradeOstData,
    private val verifyIsOtsCompletelyUpdated: VerifyIsOtsCompletelyUpdated,
    private val saveUpdatedOtsData: SaveUpdatedOtsData,
    @IOScheduler private val executorScheduler: Scheduler,
    private val logger: Logger
) {
    fun getCompletable(attestation: Attestation): Completable =
        upgradeOstData.getSingle(attestation.otsData)
            .flatMapCompletable { updatedOtsData ->
                if (!attestation.otsData.contentEquals(updatedOtsData)) {
                    attestation.otsData = updatedOtsData
                    verifyIsOtsCompletelyUpdated.getSingle(updatedOtsData)
                        .flatMapCompletable {
                            attestation.isOtsUpdated = it
                            saveUpdatedOtsData.getCompletable(attestation)
                        }
                } else Completable.complete()
            }.doOnError { logger.log("Error on ${this::class.qualifiedName}: $it") }
            .subscribeOn(executorScheduler)

}