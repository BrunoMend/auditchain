package domain.datarepository

import domain.model.Attestation
import domain.model.BlockchainPublication
import io.reactivex.rxjava3.core.Single

interface TimestampDataRepository {
    fun stampData(data: ByteArray): Single<ByteArray>
    fun upgradeOstData(attestation: Attestation): Single<Attestation>
    fun checkIsOtsComplete(attestation: Attestation): Single<Attestation>
    fun verifyStamp(attestation: Attestation): Single<List<BlockchainPublication>>
}