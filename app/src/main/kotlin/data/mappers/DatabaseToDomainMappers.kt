package data.mappers

import data.database.model.AttestationDM
import data.database.model.SourceDM
import data.database.model.StampExceptionDM
import domain.model.*

fun AttestationDM.toDomainModel(): Attestation =
    Attestation(
        TimeInterval(dateStart, dateEnd),
        source.toDomainModel(),
        dateTimestamp,
        dataSignature,
        otsData,
        dateOtsComplete,
        id
    )

fun StampExceptionDM.toDomainModel(): StampException =
    StampException(
        TimeInterval(dateStart, dateEnd),
        source.toDomainModel(),
        exception,
        dateException,
        id
    )

fun SourceDM.toDomainModel(): Source =
    when (this) {
        SourceDM.ELASTICSEARCH -> Source.ELASTICSEARCH
    }