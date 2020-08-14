package domain.usecase

import domain.datarepository.TimestampDataRepository
import domain.model.AttestationConfiguration
import domain.model.TimestampData
import domain.model.TimestampResult
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampData @Inject constructor(
    private val timestampDataRepository: TimestampDataRepository,
    private val attestationConfiguration: AttestationConfiguration
) : SingleUseCase<TimestampResult, StampData.Request>() {

    override fun getRawSingle(request: Request): Single<TimestampResult> {
        val dataSignature = request.timestampData.sing(attestationConfiguration.privateKey)
        return timestampDataRepository.stampData(dataSignature)
            .flatMap { Single.just(TimestampResult(dataSignature, it)) }
    }

    data class Request(val timestampData: TimestampData)
}