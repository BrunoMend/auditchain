package data.mappers

import data.database.model.AttestationDM
import data.database.model.SourceDM
import data.database.model.StampExceptionDM
import domain.model.Attestation
import domain.model.Source
import domain.model.SourceParam
import domain.model.StampException

fun Attestation.toDatabaseModel(): AttestationDM =
    AttestationDM(
        timeInterval.startAt,
        timeInterval.finishIn,
        source.toDatabaseModel(),
        dateTimestamp,
        dataSignature,
        otsData,
        isOtsComplete,
        sourceParams?.toDatabaseModel(),
        id
    )

fun StampException.toDatabaseModel(): StampExceptionDM =
    StampExceptionDM(
        timeInterval.startAt,
        timeInterval.finishIn,
        source.toDatabaseModel(),
        exception,
        dateException,
        processed,
        sourceParams?.toDatabaseModel(),
        id
    )

fun Source.toDatabaseModel(): SourceDM =
    when (this) {
        Source.ELASTICSEARCH -> SourceDM.ELASTICSEARCH
        Source.POSTGRES -> SourceDM.POSTGRES
    }

fun Map<SourceParam, String>.toDatabaseModel(): Map<String, String> =
    map { it.key.toString() to it.value }.toMap()