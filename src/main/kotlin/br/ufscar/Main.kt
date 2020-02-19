package br.ufscar

import com.eternitywall.ots.DetachedTimestampFile
import com.eternitywall.ots.OpenTimestamps
import com.eternitywall.ots.op.OpSHA256
import java.io.*
import java.util.*


//const val FILEPATH: String = "C:\\Users\\bruno\\Desktop\\obj"
//const val FILE: String = "HASH PARA TESTAR TIMESTAMP 4"

fun main(args: Array<String>) {
    getCommand()
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
        "verifyStamp" -> {
            verifyStamp()
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
    println("Which file do you want to commitment?")
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

fun verifyStamp() {
    println("Provide the original file")
    val originalFilePath = readLine() ?: throw NullPointerException()
    println("Provide the ots file")
    val otsFilePath = readLine() ?: throw NullPointerException()

    val originalFile = File(originalFilePath)
    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray

    val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), originalFile)
    val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)

    println(OpenTimestamps.info(detachedOts))

    val result = OpenTimestamps.verify(detachedOts,detachedFile)
    if (result == null || result.isEmpty()) {
        println("Pending or Bad attestation")
    } else {
        result.forEach{(k, v) -> println("Success! $k attests data existed as of ${Date(v.timestamp * 1000)}")}
    }
}

fun showCommands() {
    println(
        "Write 'stamp' to stamp a file; \n" +
                "Write 'getInfo' to get info from a file previously stamped; \n" +
                "Write 'verifyStamp' to verify the integrity from a file previously stamped."
    )
}

// TODO criar classe separada somente para lidar com I/O
fun writeObjectToFile(obj: ByteArray, filePath: String) {
    try {
        val fileOut = FileOutputStream(filePath)
        val objectOut = ObjectOutputStream(fileOut)
        objectOut.writeObject(obj)
        objectOut.close()
        println("The Object $obj  was successfully written to a file")
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun readObjectFromFile(filePath: String): Any? {
    return try {
        val fileIn = FileInputStream(filePath)
        val objectIn = ObjectInputStream(fileIn)
        val obj = objectIn.readObject()
        println("The Object $obj has been read from the file")
        objectIn.close()
        obj
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
        null
    }
}
