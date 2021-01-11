package domain.utility

import domain.exception.logLevel
import domain.exception.logMessage
import java.util.logging.FileHandler
import java.util.logging.Level

fun printExceptionLogInFile(className: String, exception: Throwable) {
    printLogInFile(
        className,
        System.getenv("EXCEPTION_LOG_FILE_PATH"),
        exception.logLevel,
        exception.logMessage
    )
}

fun printMessageLogInFile(className: String, message: String) {
    printLogInFile(
        className,
        System.getenv("MESSAGE_LOG_FILE_PATH"),
        Level.INFO,
        message
    )
}

private fun printLogInFile(className: String, filePath: String, loggerLevel: Level, message: String) {
    val logger = java.util.logging.Logger.getLogger(className)
    val fileHandler = FileHandler(filePath, true)
    logger.addHandler(fileHandler)
    logger.log(loggerLevel, message)
    logger.removeHandler(fileHandler)
    fileHandler.close()
}