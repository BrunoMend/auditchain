package data.repository

import data.blockchain.OpenTimestampsDataSource
import data.mappers.toDomain
import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TimestampRepository @Inject constructor(private val openTimestampsDataSource: OpenTimestampsDataSource) :
    TimestampDataRepository {

    override fun stampData(data: ByteArray): Single<ByteArray> =
        openTimestampsDataSource.stamp(data)

    override fun verifyStamp(originalData: ByteArray, otsData: ByteArray): Single<List<Attestation>> =
        openTimestampsDataSource.verify(originalData, otsData)
            .map { it.toDomain() }
}