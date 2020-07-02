package data.mappers

import data.database.infrastructure.EnumSource
import data.database.model.AttestationDM
import data.database.model.StampExceptionDM
import domain.model.Attestation
import domain.model.Source
import domain.model.StampException

fun Attestation.toDatabaseModel(): AttestationDM =
    AttestationDM(
        timeInterval.startAt,
        timeInterval.finishIn,
        source.toDatabaseModel(),
        dateTimestamp,
        otsData
    )

fun StampException.toDatabaseModel(): StampExceptionDM =
    StampExceptionDM(
        timeInterval.startAt,
        timeInterval.finishIn,
        source.toDatabaseModel(),
        exception,
        dateException
    )

fun Source.toDatabaseModel(): String =
    when (this) {
        Source.ELASTICSEARCH -> EnumSource.ELASTICSEARCH
        Source.POSTGRES -> EnumSource.POSTEGRES
    }