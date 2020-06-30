package data.mappers

import data.database.infrastructure.TableAttestation
import data.database.model.AttestationDM
import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import java.lang.IllegalArgumentException

fun AttestationDM.toDomainModel() =
    Attestation(
        TimeInterval(dateStart, dateEnd),
        when(source) {
            TableAttestation.SOURCE_ELASTICSEARCH -> Source.ELASTICSEARCH
            TableAttestation.SOURCE_POSTEGRES -> Source.POSTGRES
            else -> throw IllegalArgumentException("Source not mapped: $source")
        },
        dateTimestamp,
        otsData
    )

