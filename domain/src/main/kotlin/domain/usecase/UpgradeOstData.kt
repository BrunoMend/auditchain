package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpgradeOstData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository
) : SingleUseCase<Attestation, UpgradeOstData.Request>() {

    override fun getRawSingle(request: Request): Single<Attestation> =
        timestampDataRepository.upgradeOstData(request.attestation)

    data class Request(val attestation: Attestation)
}