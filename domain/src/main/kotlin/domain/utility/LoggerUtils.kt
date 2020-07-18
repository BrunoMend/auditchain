package domain.utility

import java.util.logging.FileHandler

fun getFileLogger(className: String): java.util.logging.Logger {
    val logger = java.util.logging.Logger.getLogger(className)
    logger.addHandler(FileHandler("C:\\ots\\logs.log", true))
    return logger
}