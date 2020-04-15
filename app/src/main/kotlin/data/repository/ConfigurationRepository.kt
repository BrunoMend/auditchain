package data.repository

import data.file.PropertiesFileDataSource
import data.mappers.toDomain
import domain.datarepository.ConfigurationDataRepository
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration
import io.reactivex.rxjava3.core.Single

class ConfigurationRepository(private val propertiesFileDataSource: PropertiesFileDataSource): ConfigurationDataRepository {

    override fun getElasticsearchConfiguration(): Single<ElasticsearchConfiguration> =
        propertiesFileDataSource.getElasticsearchConfiguration().map { it.toDomain() }

    override fun getAttestationConfiguration(): Single<AttestationConfiguration> =
        propertiesFileDataSource.getAttestationConfiguration().map { it.toDomain() }
}