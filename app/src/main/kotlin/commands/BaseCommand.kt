package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.ExpectedException
import domain.exception.errorName
import domain.utility.printMessageLogInFile
import okhttp3.OkHttpClient

abstract class BaseCommand(private val client: OkHttpClient) : CliktCommand() {

    private val verbose: Boolean
            by option("-v", "--verbose").flag()

    protected fun printMsg(msg: String) {
        println(msg)
        printMessageLogInFile(this::class.simpleName ?: "", msg)
    }

    protected fun printVerbose(msg: String) {
        if (verbose) printMsg(msg)
    }

    protected fun Throwable.printError() {
        when (this) {
            is ExpectedException -> printMsg(this.message!!)
            else -> printMsg(
                "Unexpected error: ${this.errorName} :: ${this.message}.\n" +
                        "See logs.log for more information."
            )
        }
    }

    protected fun printProcessCompleted() {
        printMsg(".\n.\n.\nProcess completed")
    }

    protected fun printSeparatorLine() {
        printMsg("-------------------------------------------------------------------")
    }

    protected fun printVerboseSeparatorLine() {
        if (verbose) printSeparatorLine()
    }

    protected fun releaseResources() {
        // release resources to finish the program properly

        //https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/
        client.dispatcher().executorService().shutdown()
        client.connectionPool().evictAll()
        client.cache()?.close()
    }
}