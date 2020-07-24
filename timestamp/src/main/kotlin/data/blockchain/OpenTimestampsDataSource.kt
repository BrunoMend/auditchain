package data.blockchain

import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.VerifyResult
import com.eternitywall.ots.attestation.PendingAttestation
import com.eternitywall.ots.op.OpSHA256
import domain.di.ComputationScheduler
import domain.di.IOScheduler
import domain.exception.BadAttestationException
import domain.exception.DataNotMatchOriginalException
import domain.exception.PendingAttestationException
import domain.model.Attestation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class OpenTimestampsDataSource @Inject constructor(
    @IOScheduler private val ioScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler
) {
    //TODO
    // change OpSHA256 to OpKECCAK256
    // getting java.security.NoSuchAlgorithmException: keccak256 MessageDigest not available

    fun stamp(data: ByteArray): Single<ByteArray> =
        Single.fromCallable {
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), data)
            OpenTimestamps.stamp(detachedFile)
            detachedFile.serialize()
        }.subscribeOn(ioScheduler)

    fun verify(originalData: ByteArray, attestation: Attestation): Single<HashMap<VerifyResult.Chains, VerifyResult>> =
        Single.fromCallable {
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), originalData)
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(attestation.otsData)
            if (!Arrays.equals(detachedOts.fileDigest(), detachedFile.fileDigest()))
                throw DataNotMatchOriginalException(attestation)
            val result = OpenTimestamps.verify(detachedOts, detachedFile)
            result
        }.flatMap { result ->
            if (result == null || result.isEmpty())
                verifyIsOtsCompletelyUpdated(attestation)
                    .andThen(Single.fromCallable {
                        if (attestation.isOtsUpdated) throw BadAttestationException(attestation)
                        else throw PendingAttestationException(attestation)
                    })
            else Single.just(result)
        }.subscribeOn(ioScheduler)

    fun upgradeOtsData(attestation: Attestation): Completable =
        Single.fromCallable {
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(attestation.otsData)
            OpenTimestamps.upgrade(detachedOts)
            detachedOts.serialize()
        }.flatMapCompletable { updatedOtsData ->
            if (!attestation.otsData.contentEquals(updatedOtsData)) {
                attestation.otsData = updatedOtsData
                verifyIsOtsCompletelyUpdated(attestation)
            } else Completable.complete()
        }.subscribeOn(ioScheduler)

    fun verifyIsOtsCompletelyUpdated(attestation: Attestation): Completable =
        Completable.fromAction {
            val timestamp = DetachedTimestampFile.deserialize(attestation.otsData).timestamp
            for (subStamp in timestamp.directlyVerified()) {
                for (subStampAttestation in subStamp.attestations) {
                    if (subStampAttestation is PendingAttestation && !subStamp.isTimestampComplete) {
                        attestation.isOtsUpdated = false
                        return@fromAction
                    }
                }
            }
            attestation.isOtsUpdated = true
        }.subscribeOn(computationScheduler)
}