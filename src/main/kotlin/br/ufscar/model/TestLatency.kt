package br.ufscar.model

import br.ufscar.utils.createFile
import br.ufscar.utils.log
import br.ufscar.utils.readObjectFromFile
import br.ufscar.utils.writeObjectToFile
import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.op.OpSHA256
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.timerTask

class TestLatency(private val folder: String) {

    private val filesName: String = "latencyTest"

    private var i = 1
    private var stampedAt: LocalDateTime? = null
    private var filePath = ""
    private var otsPath = ""

    private val logFilePath: String by lazy { "${folder}\\log${LocalDate.now()}.txt" }

    init {
        testLatencyStamp()
    }

    //stamp a file
    fun testLatencyStamp() {
        filePath = "$folder\\$filesName$i.txt"
        otsPath = "$folder\\$filesName$i.txt.ots"

        createFile(filePath, "This is the test number $i")
        val file = File(filePath)
        val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), file)
        OpenTimestamps.stamp(detachedFile)
        stampedAt = LocalDateTime.now()
        log("stamp", "File $filePath stamped at: $stampedAt", logFilePath)
        writeObjectToFile(detachedFile.serialize(), otsPath)
        testLatencyVerify()
    }

//private var result = false

    fun testLatencyVerify() {
        Timer().schedule(timerTask {
            val otsFileObj = readObjectFromFile(otsPath) as ByteArray
            val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)

            if (OpenTimestamps.upgrade(detachedOts)) println("Timestamp upgraded")

            val result = OpenTimestamps.verify(detachedOts.timestamp)
            if (result == null || result.isEmpty()) {
//        if(!result) {
                log(
                    "verify",
                    "$filePath not posted in ${stampedAt?.until(LocalDateTime.now(), ChronoUnit.SECONDS)} seconds",
                    logFilePath
                )
//            result = true
                testLatencyVerify()
            } else {
                result.forEach { (k, v) -> println("Success! $k attests data existed as of ${Date(v.timestamp * 1000)}") }
                log(
                    "verify",
                    "$filePath POSTED! At ${LocalDateTime.now()}",
                    logFilePath
                )
                log(
                    "verify",
                    "Total time (approximately) = ${stampedAt?.until(LocalDateTime.now(), ChronoUnit.SECONDS)} seconds",
                    logFilePath
                )
                i++
//            result = false
                testLatencyStamp()
            }
        }, 25000)
    }

}