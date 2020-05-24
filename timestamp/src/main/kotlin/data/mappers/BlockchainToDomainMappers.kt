package data.mappers

import com.eternitywall.ots.VerifyResult
import domain.model.Attestation
import domain.model.Blockchain

fun VerifyResult.Chains.toDomain(): Blockchain =
    when(this){
        VerifyResult.Chains.BITCOIN -> Blockchain.BITCOIN
        VerifyResult.Chains.LITECOIN -> Blockchain.LITECOIN
        VerifyResult.Chains.ETHEREUM -> Blockchain.ETHEREUM
    }

fun HashMap<VerifyResult.Chains, VerifyResult>.toDomain(): List<Attestation> =
    this.map {
        Attestation(it.key.toDomain(), it.value.timestamp * 1000) //TODO validate value
    }