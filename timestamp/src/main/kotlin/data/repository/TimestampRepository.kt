package data.repository

import data.blockchain.OpenTimestampsDataSource
import data.mappers.toDomain
import domain.datarepository.TimestampDataRepository
import domain.model.BlockchainPublication
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TimestampRepository @Inject constructor(private val openTimestampsDataSource: OpenTimestampsDataSource) :
    TimestampDataRepository {

    override fun stampData(data: ByteArray): Single<ByteArray> =
        openTimestampsDataSource.stamp(data)

    override fun performUpdates(otsData: ByteArray): Single<Boolean> =
        openTimestampsDataSource.upgrade(otsData)
            .andThen(openTimestampsDataSource.isCompletelyUpdated(otsData))

    override fun verifyStamp(originalData: ByteArray, otsData: ByteArray): Single<List<BlockchainPublication>> =
        openTimestampsDataSource.verify(originalData, otsData)
            .map { it.toDomain() }
}