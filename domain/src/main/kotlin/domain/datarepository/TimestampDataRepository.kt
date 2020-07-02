package domain.datarepository

import domain.model.BlockchainPublication
import io.reactivex.rxjava3.core.Single

interface TimestampDataRepository {
    fun stampData(data: ByteArray): Single<ByteArray>
    fun performUpdates(otsData: ByteArray): Single<Boolean>
    fun verifyStamp(originalData: ByteArray, otsData: ByteArray): Single<List<BlockchainPublication>>
}