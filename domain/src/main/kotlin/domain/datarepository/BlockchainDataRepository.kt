package domain.datarepository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface BlockchainDataRepository {
    fun stampData(data: ByteArray, proofFileName: String): Completable
    fun verifyStamp(data: ByteArray, proofFileName: String): Single<List<Map<String, String>>>
}