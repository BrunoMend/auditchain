package data.repository

import data.file.PropertiesFileDataSource
import data.mappers.toDomain
import domain.datarepository.ConfigurationDataRepository
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ConfigurationRepository @Inject constructor(
    private val propertiesFileDataSource: PropertiesFileDataSource
) : ConfigurationDataRepository {

    //TODO save configurations on memory source

    override fun getElasticsearchConfiguration(): Single<ElasticsearchConfiguration> =
        propertiesFileDataSource.getElasticsearchConfiguration().map { it.toDomain() }

    override fun getAttestationConfiguration(): Single<AttestationConfiguration> =
        propertiesFileDataSource.getAttestationConfiguration().map { it.toDomain() }
}