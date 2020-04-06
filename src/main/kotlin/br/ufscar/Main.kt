package br.ufscar

import br.ufscar.data.remote.RetrofitInitializer
import br.ufscar.data.remote.model.ElasticQuery
import br.ufscar.utils.*
import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.op.OpSHA256
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun main(args: Array<String>) {
    getCommand()
}

fun getCommand() {
    println("------------------------//----------------------------")
    println("Write a command")
    when(readLine()){
        "stamp" -> {
            println("Which file do you want to stamp?")
            val filePath = readLine() ?: throw NullPointerException()
            stamp(filePath)
            getCommand()
        }
        "getInfo" -> {
            println("Which file do you want to get info?")
            val filePath = readLine() ?: throw NullPointerException()
            getInfo(filePath)
            getCommand()
        }
        "verifyFile" -> {
            println("Provide the original file")
            val originalFilePath = readLine() ?: throw NullPointerException()
            println("Provide the ots file")
            val otsFilePath = readLine() ?: throw NullPointerException()

            verifyFile(originalFilePath, otsFilePath)
            getCommand()
        }
        "verifyStamp" -> {
            println("Provide the ots file")
            val otsFilePath = readLine() ?: throw NullPointerException()

            verifyStamp(otsFilePath)
            getCommand()
        }
        "upgrade" -> {
            println("Provide the ots file")
            val otsFilePath = readLine() ?: throw NullPointerException()
            upgrade(otsFilePath)
            getCommand()
        }
        "testLatency" -> {
            println("Provide a folder")
            val folder = readLine() ?: throw NullPointerException()
            TestLatency(folder)
        }
        "stampRemote" -> {
            stampRemoteData()
        }
        "verifyRemote" -> {
            println("Provide the original info file")
            val originalFilePath = readLine() ?: throw NullPointerException()
            println("Provide the ots file")
            val otsFilePath = readLine() ?: throw NullPointerException()

            verifyRemoteData(originalFilePath, otsFilePath)
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
        "Type 'stamp' to stamp a file; \n" +
                "Type 'getInfo' to get info from a file previously stamped; \n" +
                "Type 'verifyFile' to verify the integrity from a file previously stamped; \n" +
                "Type 'verifyStamp' to check the veracity of a timestamp in an ots file; \n" +
                "Type 'upgrade' to upgrade incomplete remote calendar timestamps to be independently verifiable; \n" +
                "Type 'testLatency' to verify the average time to stamp a file on Blockchain; \n" +
                "Type 'stampRemote' to stamp ElasticSearch data; \n" +
                "Type 'verifyRemote' to verify ElasticSearch data"
    )
}

fun stampRemoteData() {

    //TODO get from config file
    val elasticQuery = ElasticQuery("labinfo*",
        "@timestamp",
        "2020-03-21T02:51:00".toDateMillis(),
        "2020-03-21T02:52:00".toDateMillis(),
        100)

    //TODO service as singleton
    val service = RetrofitInitializer().createElasticService()

    service.getLogs(elasticQuery.indexPattern, elasticQuery.query, 100).enqueue(object: Callback<String> {
        override fun onFailure(call: Call<String>, t: Throwable) {
            println(t.message)
        }

        override fun onResponse(call: Call<String>, response: Response<String>) {
            val logs = ElasticQuery.getRequestContent(response.body())

            if(logs.isNullOrEmpty()) {
                println("have no logs to stamp")
                return
            }

            println(logs)

            //stamp
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), logs.toByteArray())
            OpenTimestamps.stamp(detachedFile)
            writeObjectToFile(elasticQuery, "C:\\otsvalidate\\$elasticQuery")
            writeObjectToFile(detachedFile.serialize(), "C:\\otsvalidate\\$elasticQuery.ots")
            println(OpenTimestamps.info(detachedFile))

        }

    })
}

fun verifyRemoteData(originalFilePath: String, otsFilePath: String) {
    val elasticQuery: ElasticQuery = readObjectFromFile(originalFilePath) as ElasticQuery
    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray

    val service = RetrofitInitializer().createElasticService()

    service.getLogs(elasticQuery.indexPattern, elasticQuery.query, 100).enqueue(object: Callback<String> {
        override fun onFailure(call: Call<String>, t: Throwable) {
            println(t.message)
        }

        override fun onResponse(call: Call<String>, response: Response<String>) {
            val logs = ElasticQuery.getRequestContent(response.body())

            if(logs.isNullOrEmpty()) {
                println("have no logs to verify")
                return
            }

            println(logs)

            //verify
            val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), logs.toByteArray())
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)
            verifyStamp(detachedFile, detachedOts)
        }

    })
}