package domain.datarepository

import domain.model.Attestation
import domain.model.BlockchainPublication
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface TimestampDataRepository {
    fun stampData(data: ByteArray): Single<ByteArray>
    fun upgradeOstData(attestation: Attestation): Completable
    fun verifyIsOtsCompletelyUpdated(attestation: Attestation): Completable
    fun verifyStamp(originalData: ByteArray, attestation: Attestation): Single<List<BlockchainPublication>>
}