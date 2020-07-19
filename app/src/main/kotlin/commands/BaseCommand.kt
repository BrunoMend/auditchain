package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

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

    protected open fun printProcessCompleted() {
        printMsg("Process completed")
    }

    protected open fun printProcessError(error: Throwable?) {
        printMsg("Unexpected process error - $error")
    }
}