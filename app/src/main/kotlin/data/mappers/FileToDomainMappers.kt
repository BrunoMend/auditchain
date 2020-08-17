package data.mappers

import data.file.model.AttestationConfigurationFM
import data.file.model.ElasticsearchConfigurationFM
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration
import org.abstractj.kalium.keys.SigningKey
import org.abstractj.kalium.keys.VerifyKey

fun ElasticsearchConfigurationFM.toDomain() =
    ElasticsearchConfiguration(elasticHost, elasticUser, elasticPwds, indexPattern, rangeParameter, resultMaxSize)

fun AttestationConfigurationFM.toDomain(signingKeyByteArray: ByteArray, verifyKeyByteArray: ByteArray) =
    AttestationConfiguration(
        frequency,
        delay,
        tryAgainTimeout,
        maxTimeInterval,
        SigningKey(signingKeyByteArray),
        VerifyKey(verifyKeyByteArray)
    )