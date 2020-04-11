package data.repository

import domain.datarepository.BlockchainDataRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class BlockchainRepository: BlockchainDataRepository {
    override fun stampData(data: ByteArray, proofFileName: String): Completable {
        TODO("Not yet implemented")
//    .flatMapCompletable { IODataRepository.writeFile(it, proofFileName) }
    }

    override fun verifyStamp(data: ByteArray, proofFileName: String): Single<List<Map<String, String>>> {
        TODO("Not yet implemented")
    }
}