package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.ExpectedException
import domain.exception.errorName

abstract class BaseCommand : CliktCommand() {
    private val verbose: Boolean
            by option("-v", "--verbose").flag()

    protected fun printMsg(msg: String) {
        //TODO check the best way to print console messages
        println(msg)
    }

    protected fun printVerbose(msg: String) {
        if (verbose) printMsg(msg)
    }

    protected fun Throwable.printError() {
        when (this) {
            is ExpectedException -> printMsg(this.message!!)
            else -> printMsg("Unexpected error: ${this.errorName} :: ${this.message}")
        }
    }

    protected open fun printProcessCompleted() {
        printMsg("Process completed")
    }
}