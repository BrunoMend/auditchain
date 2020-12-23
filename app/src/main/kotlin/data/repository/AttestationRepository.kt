package data.repository

import data.database.AttestationDatabaseDataSource
import data.mappers.toDatabaseModel
import data.mappers.toDomainModel
import domain.datarepository.AttestationDataRepository
import domain.exception.NoAttestationException
import domain.model.Attestation
import domain.model.Source
import domain.model.SourceParam
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AttestationRepository @Inject constructor(
    private val attestationDatabaseDataSource: AttestationDatabaseDataSource
) : AttestationDataRepository {

    override fun saveAttestation(attestation: Attestation): Completable =
        attestationDatabaseDataSource.insertAttestation(attestation.toDatabaseModel())

    override fun getAttestation(
        timeInterval: TimeInterval,
        source: Source,
        sourceParams: Map<SourceParam, String>?
    ): Single<Attestation> =
        attestationDatabaseDataSource.getAttestation(
            timeInterval.startAt,
            timeInterval.finishIn,
            source.toDatabaseModel(),
            sourceParams?.toDatabaseModel()
        ).onErrorResumeNext { error ->
            if (error is NoSuchElementException) Single.error(
                NoAttestationException(
                    source,
                    sourceParams,
                    timeInterval
                )
            )
            else Single.error(error)
        }.map { it.toDomainModel() }

    override fun getIncompleteOtsAttestations(): Single<List<Attestation>> =
        attestationDatabaseDataSource.getIncompleteOtsAttestations()
            .map { it.map { it.toDomainModel() } }

    override fun updateOtsData(attestation: Attestation): Completable =
        attestationDatabaseDataSource.updateOtsData(attestation.toDatabaseModel())

    override fun getLastStampedTime(source: Source): Single<Long> =
        attestationDatabaseDataSource.getLastAttestation(source.toDatabaseModel())
            .map { it?.dateEnd ?: throw NoAttestationException(source, null, null) }
}