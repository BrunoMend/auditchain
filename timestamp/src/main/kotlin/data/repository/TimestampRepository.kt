package data.repository

import data.blockchain.OpenTimestampsDataSource
import data.mappers.toDomain
import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import domain.model.BlockchainPublication
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TimestampRepository @Inject constructor(
    private val openTimestampsDataSource: OpenTimestampsDataSource
) : TimestampDataRepository {

    override fun stampData(data: ByteArray): Single<ByteArray> =
        openTimestampsDataSource.stamp(data)

    override fun upgradeOstData(attestation: Attestation): Completable =
        openTimestampsDataSource.upgradeOtsData(attestation)

    override fun verifyIsOtsCompletelyUpdated(attestation: Attestation): Completable =
        openTimestampsDataSource.verifyIsOtsCompletelyUpdated(attestation)

    override fun verifyStamp(originalData: ByteArray, attestation: Attestation): Single<List<BlockchainPublication>> =
        openTimestampsDataSource.verify(originalData, attestation)
            .map { it.toDomain() }
}