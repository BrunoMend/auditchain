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
                if (!attestation.otsData.contentEquals(upgradedOtsData))
                    checkIsOtsComplete(attestation.apply { otsData = upgradedOtsData })
                else Single.just(attestation)
            }

    override fun checkIsOtsComplete(attestation: Attestation): Single<Attestation> =
        openTimestampsDataSource.verifyIsOtsComplete(attestation.otsData)
            .map { attestation.apply { dateOtsComplete = if(it) System.currentTimeMillis() else null } }

    override fun verifyStamp(attestation: Attestation): Single<List<BlockchainPublication>> =
        openTimestampsDataSource.verify(attestation.dataSignature, attestation.otsData)
            .onErrorResumeNext { error ->
                if (error is DataNotMatchOriginalException) Single.error(InvalidOriginalDataException(attestation))
                else Single.error(error)
            }.flatMap { result ->
                if (result == null || result.isEmpty())
                    checkIsOtsComplete(attestation)
                        .map {
                            if (attestation.dateOtsComplete != null) throw BadAttestationException(attestation)
                            else throw PendingAttestationException(attestation)
                        }
                else Single.just(result)
            }.map { it.toDomain() }
}