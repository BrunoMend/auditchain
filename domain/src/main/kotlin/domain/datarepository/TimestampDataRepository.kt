package domain.datarepository

import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface TimestampDataRepository {
    fun stampData(data: ByteArray, proofFileName: String): Completable
    fun verifyStamp(data: ByteArray, proofFileName: String): Single<List<Attestation>>
}