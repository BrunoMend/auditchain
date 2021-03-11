package data.mappers

import data.file.model.AttestationConfigurationFM
import data.file.model.ElasticsearchConfigurationFM
import domain.cryptography.SigningKey
import domain.cryptography.VerifyKey
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration

fun ElasticsearchConfigurationFM.toDomain() =
    ElasticsearchConfiguration(
        elasticHost,
        elasticUser,
        elasticPwds,
        indexPattern
    )

fun AttestationConfigurationFM.toDomain(signingKeyByteArray: ByteArray, verifyKeyByteArray: ByteArray) =
    AttestationConfiguration(
        frequency,
        delay,
        SigningKey(signingKeyByteArray),
        VerifyKey(verifyKeyByteArray)
    )