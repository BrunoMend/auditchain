package data.mappers

import data.database.model.AttestationDM
import data.database.model.SourceDM
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
        dataSignature,
        otsData,
        dateOtsComplete,
        id
    )

fun StampException.toDatabaseModel(): StampExceptionDM =
    StampExceptionDM(
        timeInterval.startAt,
        timeInterval.finishIn,
        source.toDatabaseModel(),
        exception,
        dateException,
        id
    )

fun Source.toDatabaseModel(): SourceDM =
    when (this) {
        Source.ELASTICSEARCH -> SourceDM.ELASTICSEARCH
    }