package br.ufscar.utils

import java.io.*

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

fun log(tag: String, textLog: String, logFilePath: String) {
    createFile(logFilePath)
    File(logFilePath).appendText("$tag : $textLog \n")
}