package data.mappers

import data.file.model.AttestationConfigurationFM
import data.file.model.ElasticsearchConfigurationFM
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration

fun ElasticsearchConfigurationFM.toDomain() =
    ElasticsearchConfiguration(elasticHost, elasticUser, elasticPwds, indexPattern, rangeParameter, resultMaxSize)

fun AttestationConfigurationFM.toDomain() =
    AttestationConfiguration(frequency, delay, tryAgainTimeout, maxTimeInterval, attestationFilePath)