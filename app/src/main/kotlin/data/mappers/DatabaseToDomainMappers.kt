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
        isOtsComplete,
        sourceParams?.toDomainModel(),
        id
    )

fun StampExceptionDM.toDomainModel(): StampException =
    StampException(
        TimeInterval(dateStart, dateEnd),
        source.toDomainModel(),
        exception,
        dateException,
        processed,
        sourceParams?.toDomainModel(),
        id
    )

fun SourceDM.toDomainModel(): Source =
    when (this) {
        SourceDM.ELASTICSEARCH -> Source.ELASTICSEARCH
        SourceDM.POSTGRES -> Source.POSTGRES
    }

fun Map<String, String>.toDomainModel(): Map<SourceParam, String> =
    map { SourceParam.valueOf(it.key) to it.value }.toMap()