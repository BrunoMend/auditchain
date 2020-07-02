package data.mappers

import data.database.infrastructure.EnumSource
import data.database.model.AttestationDM
import data.database.model.StampExceptionDM
import domain.model.Attestation
import domain.model.Source
import domain.model.StampException
import domain.model.TimeInterval

fun AttestationDM.toDomainModel(): Attestation =
    Attestation(
        TimeInterval(dateStart, dateEnd),
        sourceToDomainModel(source),
        dateTimestamp,
        otsData
    )

fun StampExceptionDM.toDomainModel(): StampException =
    StampException(
        TimeInterval(dateStart, dateEnd),
        sourceToDomainModel(source),
        exception,
        dateException
    )

fun sourceToDomainModel(source: String): Source =
    when (source) {
        EnumSource.ELASTICSEARCH -> Source.ELASTICSEARCH
        EnumSource.POSTEGRES -> Source.POSTGRES
        else -> throw IllegalArgumentException("Source not mapped: $source")
    }
