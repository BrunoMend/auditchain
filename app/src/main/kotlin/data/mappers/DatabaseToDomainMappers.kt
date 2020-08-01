package data.mappers

import data.database.model.AttestationDM
import data.database.model.SourceDM
import data.database.model.StampExceptionDM
import domain.model.Attestation
import domain.model.Source
import domain.model.StampException
import domain.model.TimeInterval

fun AttestationDM.toDomainModel(): Attestation =
    Attestation(
        TimeInterval(dateStart, dateEnd),
        source.toDomainModel(),
        dateTimestamp,
        otsData,
        isOtsUpdated,
        id
    )

fun StampExceptionDM.toDomainModel(): StampException =
    StampException(
        TimeInterval(dateStart, dateEnd),
        source.toDomainModel(),
        exception,
        dateException,
        processed,
        id
    )

fun SourceDM.toDomainModel(): Source =
    when (this) {
        SourceDM.ELASTICSEARCH -> Source.ELASTICSEARCH
        SourceDM.POSTGRES -> Source.POSTGRES
    }
