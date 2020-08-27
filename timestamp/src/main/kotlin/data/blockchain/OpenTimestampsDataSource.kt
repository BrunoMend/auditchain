package data.blockchain

import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.VerifyResult
import com.eternitywall.ots.attestation.PendingAttestation
import com.eternitywall.ots.op.OpSHA256
import domain.di.ComputationScheduler
import domain.di.IOScheduler
import exception.DataNotMatchOriginalException
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class OpenTimestampsDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler
) {
    fun stamp(data: ByteArray): Single<ByteArray> =
        Single.fromCallable {
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), data)
            OpenTimestamps.stamp(detachedFile)
            detachedFile.serialize()
        }.subscribeOn(ioScheduler)

    fun verify(originalData: ByteArray, otsData: ByteArray): Single<HashMap<VerifyResult.Chains, VerifyResult>> =
        Single.fromCallable {
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), originalData)
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsData)
            if (!Arrays.equals(detachedOts.fileDigest(), detachedFile.fileDigest()))
                throw DataNotMatchOriginalException(originalData, otsData)
            OpenTimestamps.verify(detachedOts, detachedFile)
        }.subscribeOn(ioScheduler)

    fun upgradeOtsData(otsData: ByteArray): Single<ByteArray> =
        Single.fromCallable {
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsData)
            OpenTimestamps.upgrade(detachedOts)
            detachedOts.serialize()
        }.subscribeOn(ioScheduler)

    fun verifyIsOtsComplete(otsData: ByteArray): Single<Boolean> =
        Single.fromCallable {
            val timestamp = DetachedTimestampFile.deserialize(otsData).timestamp
            for (subStamp in timestamp.directlyVerified()) {
                for (subStampAttestation in subStamp.attestations) {
                    if (subStampAttestation is PendingAttestation && !subStamp.isTimestampComplete) {
                        return@fromCallable false
                    }
                }
            }
            true
        }.subscribeOn(computationScheduler)
}