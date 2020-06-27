package data.repository

import data.database.AttestationDatabaseDataSource
import data.database.model.AttestationDM
import data.mappers.toDatabaseModel
import data.mappers.toDomainModel
import domain.datarepository.AttestationDataRepository
import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AttestationRepository @Inject constructor(
    private val attestationDatabaseDataSource: AttestationDatabaseDataSource
) : AttestationDataRepository {

    override fun saveAttestation(attestation: Attestation): Completable =
        attestationDatabaseDataSource.insertAttestation(attestation.toDatabaseModel())

    override fun saveEmptyAttestation(timeInterval: TimeInterval, source: Source, hasNoData: Boolean): Completable =
        attestationDatabaseDataSource.insertAttestation(
            AttestationDM(
                timeInterval.startAt,
                timeInterval.finishIn,
                source.toDatabaseModel(),
                hasNoData = hasNoData
            )
        )

    override fun getAttestation(timeInterval: TimeInterval, source: Source): Single<Attestation> =
        attestationDatabaseDataSource.getAttestation(timeInterval, source)
            .map { it.toDomainModel() }
}