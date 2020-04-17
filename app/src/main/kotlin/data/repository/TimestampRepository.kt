package data.repository

import data.blockchain.OpenTimestampsDataSource
import data.file.ObjectFileDataSource
import data.mappers.toDomain
import domain.datarepository.TimestampDataRepository
import domain.model.Attestation
import domain.model.AttestationConfiguration
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

class TimestampRepository @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val openTimestampsDataSource: OpenTimestampsDataSource,
    private val objectFileDataSource: ObjectFileDataSource
) : TimestampDataRepository {

    override fun stampData(data: ByteArray, proofFileName: String): Completable =
        Single.zip<AttestationConfiguration, ByteArray, Pair<AttestationConfiguration, ByteArray>>(
            configurationRepository.getAttestationConfiguration(), openTimestampsDataSource.stamp(data),
            BiFunction { attestationConfig, proofData -> Pair(attestationConfig, proofData) })
            .flatMapCompletable { objectFileDataSource.write(it.first.attestationFilePath, proofFileName, it.second) }

    override fun verifyStamp(data: ByteArray, proofFileName: String): Single<List<Attestation>> =
        configurationRepository.getAttestationConfiguration()
            .flatMap { objectFileDataSource.read<ByteArray>(it.attestationFilePath, proofFileName) }
            .flatMap { openTimestampsDataSource.verify(data, it) }
            .map { it.toDomain() }
}