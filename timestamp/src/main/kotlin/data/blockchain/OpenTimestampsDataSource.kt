package data.blockchain

import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.VerifyResult
import com.eternitywall.ots.attestation.PendingAttestation
import com.eternitywall.ots.op.OpKECCAK256
import com.eternitywall.ots.op.OpSHA256
import domain.di.ComputationScheduler
import domain.di.IOScheduler
import domain.exception.InvalidAttestationException
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
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpKECCAK256(), data)
            OpenTimestamps.stamp(detachedFile)
            detachedFile.serialize()
        }.subscribeOn(ioScheduler)

    fun verify(originalData: ByteArray, otsData: ByteArray): Single<HashMap<VerifyResult.Chains, VerifyResult>> =
        Single.fromCallable {
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), originalData)
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsData)
            val result = OpenTimestamps.verify(detachedOts, detachedFile)
            if (result == null || result.isEmpty())
                throw InvalidAttestationException("Pending or Bad attestation")
            result
        }.subscribeOn(ioScheduler)

    fun upgrade(otsData: ByteArray): Single<ByteArray> =
        Single.fromCallable {
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsData)
            OpenTimestamps.upgrade(detachedOts)
            detachedOts.serialize()
        }.subscribeOn(ioScheduler)

    fun isCompletelyUpdated(otsData: ByteArray): Single<Boolean> =
        Single.fromCallable {
            val timestamp = DetachedTimestampFile.deserialize(otsData).timestamp
            for (subStamp in timestamp.directlyVerified()) {
                for (attestation in subStamp.attestations) {
                    if (attestation is PendingAttestation && !subStamp.isTimestampComplete) {
                        return@fromCallable false
                    }
                }
            }
            true
        }.subscribeOn(computationScheduler)

    fun getInfo(otsData: ByteArray): Single<String> =
        Single.fromCallable {
            OpenTimestamps.info(DetachedTimestampFile.deserialize(otsData))
        }.subscribeOn(ioScheduler)
}