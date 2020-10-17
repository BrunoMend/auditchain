package domain.utility

import java.util.logging.FileHandler

const val LOG_FILE_PATH = "C:\\ots\\debug\\logs.log" //debug
//const val LOG_FILE_PATH = "./logs.log" //prod

fun getFileLogger(className: String): java.util.logging.Logger {
    val logger = java.util.logging.Logger.getLogger(className)
    logger.addHandler(FileHandler(LOG_FILE_PATH, true))
    return logger
}