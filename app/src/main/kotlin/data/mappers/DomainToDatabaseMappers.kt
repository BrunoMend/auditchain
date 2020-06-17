package data.mappers

import data.database.infrastructure.TableAttestation
import data.database.infrastructure.TableBlockchainPublication
import data.database.model.AttestationDM
import data.database.model.BlockchainPublicationDM
import domain.model.Attestation
import domain.model.Blockchain
import domain.model.BlockchainPublication
import domain.model.Source

fun Attestation.toDatabaseModel() =
    AttestationDM(
        timeInterval.startAt,
        timeInterval.finishIn,
        source.toDatabaseModel(),
        dateTimestamp,
        otsData
    )

fun Source.toDatabaseModel() =
    when(this) {
        Source.ELASTICSEARCH -> TableAttestation.SOURCE_ELASTICSEARCH
        Source.POSTGRES -> TableAttestation.SOURCE_POSTEGRES
    }

fun BlockchainPublication.toDatabaseModel() =
    BlockchainPublicationDM(
        blockchain.toDatabaseModel(),
        blockId,
        datePublication
    )

fun Blockchain.toDatabaseModel() =
    when(this) {
        Blockchain.BITCOIN -> TableBlockchainPublication.BLOCKCHAIN_BITCOIN
        Blockchain.ETHEREUM -> TableBlockchainPublication.BLOCKCHAIN_ETHEREUM
        Blockchain.LITECOIN -> TableBlockchainPublication.BLOCKCHAIN_LITECOIN
    }