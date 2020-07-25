package data.repository

import data.blockchain.OpenTimestampsDataSource
import data.mappers.toDomain
import domain.datarepository.TimestampDataRepository
import domain.exception.BadAttestationException
import domain.exception.InvalidOriginalDataException
import domain.exception.PendingAttestationException
import domain.model.Attestation
import domain.model.BlockchainPublication
import exception.DataNotMatchOriginalException
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TimestampRepository @Inject constructor(
    private val openTimestampsDataSource: OpenTimestampsDataSource
) : TimestampDataRepository {

    override fun stampData(data: ByteArray): Single<ByteArray> =
        openTimestampsDataSource.stamp(data)

    override fun upgradeOstData(attestation: Attestation): Single<Attestation> =
        openTimestampsDataSource.upgradeOtsData(attestation.otsData)
            .flatMap { upgradedOtsData ->
                if (!attestation.otsData.contentEquals(upgradedOtsData)) {
                    attestation.otsData = upgradedOtsData
                    verifyIsOtsComplete(attestation)
                        .andThen(Single.just(attestation))
                } else Single.just(attestation)
            }

    override fun verifyIsOtsComplete(attestation: Attestation): Completable =
        openTimestampsDataSource.verifyIsOtsComplete(attestation.otsData)
            .flatMapCompletable { isComplete ->
                Completable.fromAction { attestation.isOtsComplete = isComplete }
            }

    override fun verifyStamp(originalData: ByteArray, attestation: Attestation): Single<List<BlockchainPublication>> =
        openTimestampsDataSource.verify(originalData, attestation.otsData)
            .onErrorResumeNext { error ->
                when (error) {
                    is DataNotMatchOriginalException -> Single.error(InvalidOriginalDataException(attestation))
                    else -> Single.error(error)
                }
            }.flatMap { result ->
                if (result == null || result.isEmpty())
                    verifyIsOtsComplete(attestation)
                        .andThen(Single.fromCallable {
                            if (attestation.isOtsComplete) throw BadAttestationException(attestation)
                            else throw PendingAttestationException(attestation)
                        })
                else Single.just(result)
            }.map { it.toDomain() }
}