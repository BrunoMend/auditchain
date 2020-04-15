package data.repository

import data.blockchain.OpenTimestampsDataSource
import data.file.ObjectFileDataSource
import data.mappers.toDomain
import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import domain.model.AttestationConfiguration
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class TimestampRepository(
    private val attestationConfiguration: AttestationConfiguration,
    private val openTimestampsDataSource: OpenTimestampsDataSource,
    private val objectFileDataSource: ObjectFileDataSource
) : TimestampDataRepository {

    override fun stampData(data: ByteArray, proofFileName: String): Completable =
        openTimestampsDataSource.stamp(data)
            .flatMapCompletable { objectFileDataSource.write(attestationConfiguration.attestationFilePath, proofFileName, it) }

    override fun verifyStamp(data: ByteArray, proofFileName: String): Single<List<Attestation>> =
        objectFileDataSource.read<ByteArray>(attestationConfiguration.attestationFilePath, proofFileName)
            .flatMap { openTimestampsDataSource.verify(data, it) }
            .map { it.toDomain() }
}