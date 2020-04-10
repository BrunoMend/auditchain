package common

import common.utility.toDateMillis
import data.remote.infrastructure.RetrofitInitializer
import data.remote.model.ElasticQuery
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

fun main(args: Array<String>) {
    val service = RetrofitInitializer().createElasticService()

    TimerNotifier()
        .getTimerObservable()
        .map { ElasticQuery(it.first, it.second) }
        .flatMapSingle { Single.just(Pair(it, service.getLogs(it.indexPattern, it.query, it.size))) }
        .map { Pair(it.first, ElasticQuery.getRequestContent(it.second.blockingGet())) }
        .subscribeOn(Schedulers.computation())
        .subscribe(
            { pair ->
                val elasticQuery = pair.first
                val logs = pair.second
                if (logs.isNullOrEmpty() || logs == "[]")
                    println("have no logs to stamp")
                else
                    println(logs)
//                println("unsaved: $elasticQuery")
//                ObjectIO()
//                    .write(elasticQuery.toString().toFileName(), elasticQuery)
//                    .blockingSubscribe({ println("saved with success")}, {println("error on save: $it")})
//                ObjectIO().read<ElasticQuery>(elasticQuery.toString().toFileName()).subscribe ({ println("saved: $it") }, {println("error on read: $it")})
                    //stamp
//                    val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), logs.toByteArray())
//                    OpenTimestamps.stamp(detachedFile)
//                    writeObjectToFile(elasticQuery,"C:\\ots\\validate\\$elasticQuery")
//                    writeObjectToFile(detachedFile.serialize(),"C:\\ots\\validate\\$elasticQuery.ots")
//                    println(OpenTimestamps.info(detachedFile))

            },
            { error ->
                println(error.message)
            }
        )

    getCommand()
}

fun getCommand() {
    println("------------------------//----------------------------")
    println("Write a command")
    when(readLine()){
        "verifyElastic" -> {
            //todo verificar quais arquivos serão necessários validar
            // validar arquivo por arquivo
            println("Provide initial date time (yyyy/MM/dd-HH:mm)")
            val initialMoment = (readLine() ?: throw NullPointerException()).toDateMillis("yyyy/MM/dd-HH:mm")
            println("Provide final date time (yyyy/MM/dd-HH:mm)")
            val finalMoment = (readLine() ?: throw NullPointerException()).toDateMillis("yyyy/MM/dd-HH:mm")



        }
        "?" -> {
            showCommands()
            getCommand()
        }
        else -> {
            println("Command not recognized")
            showCommands()
            getCommand()
        }
    }
}

fun showCommands() {
    println(
        "Type 'verifyElastic' to verify ElasticSearch data"
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