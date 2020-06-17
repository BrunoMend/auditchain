package data.mappers

import data.database.infrastructure.TableAttestation
import data.database.infrastructure.TableBlockchainPublication
import data.database.model.AttestationDM
import data.database.model.BlockchainPublicationDM
import domain.model.*
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
        otsData,
        blockchainPublications?.map { it.toDomainModel() } ?: listOf()
    )

fun BlockchainPublicationDM.toDomainModel() =
    BlockchainPublication(
        when(blockchain) {
            TableBlockchainPublication.BLOCKCHAIN_BITCOIN -> Blockchain.BITCOIN
            TableBlockchainPublication.BLOCKCHAIN_ETHEREUM -> Blockchain.ETHEREUM
            TableBlockchainPublication.BLOCKCHAIN_LITECOIN -> Blockchain.LITECOIN
            else -> throw IllegalArgumentException("Blockchain not mapped: $blockchain")
        },
        blockId,
        datePublication
    )