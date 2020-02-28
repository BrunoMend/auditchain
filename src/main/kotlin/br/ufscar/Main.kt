package br.ufscar

import br.ufscar.model.TestLatency
import br.ufscar.utils.*

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
            //TODO executar em thread separada
            TestLatency(folder)
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
                "Type 'testLatency' to verify the average time to stamp a file on Blockchain"
    )
}
