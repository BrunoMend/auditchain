package data.repository

import data.database.AttestationDatabaseDataSource
import data.mappers.toDatabaseModel
import domain.datarepository.AttestationDataRepository
import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AttestationRepository @Inject constructor(
    private val attestationDatabaseDataSource: AttestationDatabaseDataSource
): AttestationDataRepository {

    override fun saveAttestation(attestation: Attestation): Completable =
        attestationDatabaseDataSource.insertAttestation(attestation.toDatabaseModel())
            .flatMapCompletable {
                Completable.fromAction { println("new row id: $it") }
            }

    override fun getAttestation(timeInterval: TimeInterval, source: Source): Single<Attestation> {
        TODO("Not yet implemented")
    }
}