package data.mappers

import data.file.model.AttestationConfigurationFM
import data.file.model.ElasticsearchConfigurationFM
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration
import org.abstractj.kalium.keys.PrivateKey
import org.abstractj.kalium.keys.PublicKey

fun ElasticsearchConfigurationFM.toDomain() =
    ElasticsearchConfiguration(elasticHost, elasticUser, elasticPwds, indexPattern, rangeParameter, resultMaxSize)

fun AttestationConfigurationFM.toDomain(privateKeyByteArray: ByteArray, publicKeyByteArray: ByteArray) =
    AttestationConfiguration(
        frequency,
        delay,
        tryAgainTimeout,
        maxTimeInterval,
        PrivateKey(privateKeyByteArray),
        PublicKey(publicKeyByteArray)
    )