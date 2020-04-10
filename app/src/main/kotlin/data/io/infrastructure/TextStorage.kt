package data.io.infrastructure

import java.io.File
import java.security.InvalidParameterException

class TextStorage {

    private fun createFile(filePathName: String) {
        val file = File(filePathName)
        if (!file.exists()) file.createNewFile()
    }

    fun upsertLine(filePathName: String, content: String) {
        createFile(filePathName)
        File(filePathName).appendText("$content \n")
    }

    fun getContent(filePathName: String, filter: String, filterType: FilterType): List<String> {
        val file = File(filePathName)
        if (!file.exists()) throw InvalidParameterException("File $filePathName doesn't exists")
        return if (filter.isEmpty()) file.readLines()
        else when (filterType) {
            FilterType.START_WITH -> file.readLines().filter { it.startsWith(filter) }
            FilterType.CONTAINS -> file.readLines().filter { it.contains(filter) }
        }
    }

    enum class FilterType {
        START_WITH,
        CONTAINS
    }
}