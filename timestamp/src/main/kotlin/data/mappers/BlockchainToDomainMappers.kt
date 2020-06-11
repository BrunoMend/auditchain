package data.mappers

import com.eternitywall.ots.VerifyResult
import domain.model.Blockchain
import domain.model.BlockchainPublication

fun VerifyResult.Chains.toDomain(): Blockchain =
    when(this){
        VerifyResult.Chains.BITCOIN -> Blockchain.BITCOIN
        VerifyResult.Chains.LITECOIN -> Blockchain.LITECOIN
        VerifyResult.Chains.ETHEREUM -> Blockchain.ETHEREUM
    }

fun HashMap<VerifyResult.Chains, VerifyResult>.toDomain(): List<BlockchainPublication> =
    this.map {
        BlockchainPublication(it.key.toDomain(), "TODO",it.value.timestamp * 1000) //TODO get block id
    }