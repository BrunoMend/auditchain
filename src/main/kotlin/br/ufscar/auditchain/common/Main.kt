package br.ufscar.auditchain.common

import br.ufscar.auditchain.data.io.writeObjectToFile
import br.ufscar.auditchain.data.remote.infrastructure.RetrofitInitializer
import br.ufscar.auditchain.data.remote.model.ElasticQuery
import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.op.OpSHA256
import io.reactivex.rxjava3.core.Single

fun main(args: Array<String>) {
    val service = RetrofitInitializer().createElasticService()

    TimerNotifier(Config.frequency, Config.delay)
        .getTimerObservable()
        .map { instants ->
            ElasticQuery(
                Config.indexPattern,
                Config.rangeParameter,
                instants.first,
                instants.second,
                Config.resultMaxSize
            )
        }.flatMapSingle { Single.just(Pair(it, service.getLogs(it.indexPattern, it.query, it.size))) }
        .map { Pair<ElasticQuery, String?>(it.first, ElasticQuery.getRequestContent(it.second.blockingGet())) }
        .subscribe(
            { pair ->
                val elasticQuery = pair.first
                val logs = pair.second
                if (logs.isNullOrEmpty() || logs == "[]")
                    println("have no logs to stamp")
                else {
                    println(logs)
                    println(elasticQuery)
                    //stamp
                    val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), logs.toByteArray())
                    OpenTimestamps.stamp(detachedFile)
                    writeObjectToFile(
                        elasticQuery,
                        "C:\\ots\\validate\\$elasticQuery"
                    )
                    writeObjectToFile(
                        detachedFile.serialize(),
                        "C:\\ots\\validate\\$elasticQuery.ots"
                    )
                    println(OpenTimestamps.info(detachedFile))
                }
            },
            { error ->
                println(error.message)
            }
        )
}


//fun verifyRemoteData(originalFilePath: String, otsFilePath: String) {
//    val elasticQuery: ElasticQuery = readObjectFromFile(
//        originalFilePath
//    ) as ElasticQuery
//    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray
//
//    val service = RetrofitInitializer().createElasticService()
//
//    service.getLogs(elasticQuery.indexPattern, elasticQuery.query, 100).enqueue(object: Callback<String> {
//        override fun onFailure(call: Call<String>, t: Throwable) {
//            println(t.message)
//        }
//
//        override fun onResponse(call: Call<String>, response: Response<String>) {
//            val logs = ElasticQuery.getRequestContent(response.body())
//
//            if(logs.isNullOrEmpty()) {
//                println("have no logs to verify")
//                return
//            }
//
//            println(logs)
//
//            //verify
//            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), logs.toByteArray())
//            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)
//            verifyStamp(detachedFile, detachedOts)
//        }
//
//    })
//}