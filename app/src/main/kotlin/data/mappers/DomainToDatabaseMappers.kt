package data.mappers

import data.database.infrastructure.TableAttestation
import data.database.model.AttestationDM
import domain.model.Attestation
import domain.model.Source

fun Attestation.toDatabaseModel() =
    AttestationDM(
        0,
        timeInterval.startAt,
        timeInterval.finishIn,
        source.toDatabaseModel(),
        otsData,
        dateTimestamp
    )

fun Source.toDatabaseModel() =
    when(this) {
        Source.ELASTICSEARCH -> TableAttestation.SOURCE_ELASTICSEARCH
        Source.POSTGRES -> TableAttestation.SOURCE_POSTEGRES
    }