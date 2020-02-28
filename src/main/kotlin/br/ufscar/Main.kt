package br.ufscar


import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.op.OpSHA256
import java.io.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.timerTask

private const val filesName: String = "latencyTest"

private var i = 1
private var stampedAt: LocalDateTime? = null
private var filePath = ""
private var otsPath = ""

private lateinit var folder: String
private val logFilePath: String by lazy { "$folder\\log${LocalDate.now()}.txt" }

fun main(args: Array<String>) {
//    getCommand()
    println("Provide a folder")
    folder = readLine() ?: throw NullPointerException()
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
    log("stamp", "File $filePath stamped at: $stampedAt")
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
            log("verify","$filePath not posted in ${stampedAt?.until(LocalDateTime.now(), ChronoUnit.SECONDS)} seconds")
//            result = true
            testLatencyVerify()
        } else {
            result.forEach { (k, v) -> println("Success! $k attests data existed as of ${Date(v.timestamp * 1000)}") }
            log("verify","$filePath POSTED! At ${LocalDateTime.now()}")
            log("verify", "Total time (approximately) = ${stampedAt?.until(LocalDateTime.now(), ChronoUnit.SECONDS)} seconds")
            i++
//            result = false
            testLatencyStamp()
        }
    }, 25000)
}


fun getCommand() {
    println("------------------------//----------------------------")
    println("Write a command")
    when(readLine()){
        "stamp" -> {
            stamp()
            getCommand()
        }
        "getInfo" -> {
            getInfo()
            getCommand()
        }
        "verifyFile" -> {
            verifyFile()
            getCommand()
        }
        "verifyStamp" -> {
            verifyStamp()
            getCommand()
        }
        "upgrade" -> {
            upgrade()
            getCommand()
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

fun stamp() {
    println("Which file do you want to stamp?")
    val filePath = readLine() ?: throw NullPointerException()
    val file = File(filePath)
    val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), file)
    OpenTimestamps.stamp(detachedFile)
    writeObjectToFile(detachedFile.serialize(), "$filePath.ots")
    println(OpenTimestamps.info(detachedFile))
}

fun getInfo() {
    println("Which file do you want to get info?")
    val filePath = readLine() ?: throw NullPointerException()
    val detachedFile = DetachedTimestampFile.deserialize(readObjectFromFile(filePath) as ByteArray)
    println(OpenTimestamps.info(detachedFile))
}

fun verifyFile() {
    println("Provide the original file")
    val originalFilePath = readLine() ?: throw NullPointerException()
    println("Provide the ots file")
    val otsFilePath = readLine() ?: throw NullPointerException()

    val originalFile = File(originalFilePath)
    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray

    val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), originalFile)
    val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)

    if(OpenTimestamps.upgrade(detachedOts)) println("Timestamp upgraded")

//    println(OpenTimestamps.info(detachedOts))

    val result = OpenTimestamps.verify(detachedOts,detachedFile)
    if (result == null || result.isEmpty()) {
        println("Pending or Bad attestation")
    } else {
        result.forEach{(k, v) -> println("Success! $k attests data existed as of ${Date(v.timestamp * 1000)}")}
    }
}

fun verifyStamp() {
    println("Provide the ots file")
    val otsFilePath = readLine() ?: throw NullPointerException()

    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray
    val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)

    if(OpenTimestamps.upgrade(detachedOts)) println("Timestamp upgraded")

//    println(OpenTimestamps.info(detachedOts))

    val result = OpenTimestamps.verify(detachedOts.timestamp)
    if (result == null || result.isEmpty()) {
        println("Pending or Bad attestation")
    } else {
        result.forEach{(k, v) -> println("Success! $k attests data existed as of ${Date(v.timestamp * 1000)}")}
    }
}

fun upgrade(){
    println("Provide the ots file")
    val otsFilePath = readLine() ?: throw NullPointerException()
    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray
    val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)
    val changed = OpenTimestamps.upgrade(detachedOts)
    if (!changed) {
        println("Timestamp not upgraded")
    } else {
        writeObjectToFile(detachedOts.serialize(), otsFilePath)
        println("Timestamp upgraded")
    }
}

fun showCommands() {
    println(
        "Type 'stamp' to stamp a file; \n" +
                "Type 'getInfo' to get info from a file previously stamped; \n" +
                "Type 'verifyFile' to verify the integrity from a file previously stamped; \n" +
                "Type 'verifyStamp' to check the veracity of a timestamp in an ots file; \n" +
                "Type 'upgrade' to upgrade incomplete remote calendar timestamps to be independently verifiable"
    )
}

// TODO criar classe separada somente para lidar com I/O
fun writeObjectToFile(obj: ByteArray, filePath: String) {
    try {
        val fileOut = FileOutputStream(filePath)
        val objectOut = ObjectOutputStream(fileOut)
        objectOut.writeObject(obj)
        objectOut.close()
//        println("The Object $obj  was successfully written to a file")
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun readObjectFromFile(filePath: String): Any? {
    return try {
        val fileIn = FileInputStream(filePath)
        val objectIn = ObjectInputStream(fileIn)
        val obj = objectIn.readObject()
//        println("The Object $obj has been read from the file")
        objectIn.close()
        obj
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
        null
    }
}

fun createFile(filePath: String, textInput: String? = null) {
    val file = File(filePath)
    if (!file.exists()) {
        file.createNewFile()
        textInput?.let { file.writeText(it) }
    }
//    else println("File $filePath already exists")
}

fun log(tag: String, textLog: String) {
    createFile(logFilePath)
    File(logFilePath).appendText("$tag : $textLog \n")
}