package data.file

import data.file.infrastructure.PropertiesStorage
import data.file.model.AttestationConfigurationFM
import data.file.model.ElasticsearchConfigurationFM
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class PropertiesFileDataSource @Inject constructor(private val propertiesStorage: PropertiesStorage) {
    private val configFilePath = "C:\\ots\\config.properties" //TODO \\config.properties

    fun getElasticsearchConfiguration(): Single<ElasticsearchConfigurationFM> =
        propertiesStorage.getProperties(configFilePath)
            .map {
                ElasticsearchConfigurationFM(
                    it.getProperty("elasticHost"),
                    it.getProperty("elasticUser"),
                    it.getProperty("elasticPwds"),
                    it.getProperty("indexPattern"),
                    it.getProperty("rangeParameter"),
                    it.getProperty("resultMaxSize").toInt()
                )
            }

    fun getAttestationConfiguration(): Single<AttestationConfigurationFM> =
        propertiesStorage.getProperties(configFilePath)
            .map {
                AttestationConfigurationFM(
                    it.getProperty("frequency").toLong(),
                    it.getProperty("delay").toLong(),
                    it.getProperty("tryAgainTimeout").toLong(),
                    it.getProperty("maxTimeInterval").toLong(),
                    it.getProperty("signingKey"),
                    it.getProperty("verifyKey")
                )
            }
}