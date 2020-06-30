package domain.datarepository

import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface AttestationDataRepository {
    fun saveAttestation(attestation: Attestation): Completable
    fun getAttestation(timeInterval: TimeInterval, source: Source): Single<Attestation>
}