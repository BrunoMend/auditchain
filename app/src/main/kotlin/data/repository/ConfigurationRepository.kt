package data.repository

import data.file.ObjectFileDataSource
import data.file.PropertiesFileDataSource
import data.mappers.toDomain
import domain.datarepository.ConfigurationDataRepository
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

class ConfigurationRepository @Inject constructor(
    private val propertiesFileDataSource: PropertiesFileDataSource,
    private val objectFileDataSource: ObjectFileDataSource
) : ConfigurationDataRepository {

    override fun getElasticsearchConfiguration(): Single<ElasticsearchConfiguration> =
        propertiesFileDataSource.getElasticsearchConfiguration().map { it.toDomain() }

    override fun getAttestationConfiguration(): Single<AttestationConfiguration> =
        propertiesFileDataSource.getAttestationConfiguration()
            .flatMap { attestationConfigurationFM ->
                Single.zip(
                    objectFileDataSource.read(attestationConfigurationFM.signingKeyFilePath, ""),
                    objectFileDataSource.read(attestationConfigurationFM.verifyKeyFilePath, ""),
                    BiFunction<ByteArray, ByteArray, Pair<ByteArray, ByteArray>> { signingKeyByteArray, verifyKeyByteArray ->
                        Pair(signingKeyByteArray, verifyKeyByteArray)
                    }
                ).map { keys ->
                    attestationConfigurationFM.toDomain(keys.first, keys.second)
                }
            }
}