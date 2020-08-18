package domain.usecase

import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StampElasticsearchData @Inject constructor(
    private val validateNoAttestationExists: ValidateNoAttestationExists,
    private val getElasticsearchData: GetElasticsearchData,
    private val stampData: StampData,
    private val saveAttestation: SaveAttestation,
    private val saveStampException: SaveStampException,
    private val buildStampException: BuildStampException
) : SingleUseCase<Result<Attestation>, StampElasticsearchData.Request>() {

    override fun getRawSingle(request: Request): Single<Result<Attestation>> =
        Single.just(request.timeInterval)
            .flatMap { timeInterval ->
                validateNoAttestationExists
                    .getCompletable(ValidateNoAttestationExists.Request(Source.ELASTICSEARCH, timeInterval))
                    .andThen(getElasticsearchData.getRawSingle(GetElasticsearchData.Request(timeInterval)))
                    .flatMap { data ->
                        stampData.getRawSingle(StampData.Request(data))
                            .flatMap { otsData ->
                                val attestation =
                                    Attestation(
                                        timeInterval,
                                        Source.ELASTICSEARCH,
                                        System.currentTimeMillis(),
                                        otsData
                                    )
                                saveAttestation.getRawCompletable(SaveAttestation.Request(attestation))
                                    .andThen(Single.just(attestation))
                            }
                    }
                    .map { Result.success(it) }
            }
            .onErrorResumeNext { error ->
                buildStampException.getRawSingle(
                    BuildStampException.Request(
                        Source.ELASTICSEARCH,
                        request.timeInterval,
                        error
                    )
                )
                    .flatMapCompletable { saveStampException.getRawCompletable(SaveStampException.Request(it)) }
                    .andThen(Single.just(Result.failure(error)))
            }

    data class Request(val timeInterval: TimeInterval)
}