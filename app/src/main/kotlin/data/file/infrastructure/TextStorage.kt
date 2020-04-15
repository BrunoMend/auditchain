package data.file.infrastructure

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.security.InvalidParameterException

class TextStorage {

    private fun createFile(filePathName: String): Completable =
        Completable.fromAction {
            val file = File(filePathName)
            if (!file.exists()) file.createNewFile()
        }

    fun insertLine(filePathName: String, content: String): Completable =
        Completable.fromAction {
            createFile(filePathName)
            File(filePathName).appendText("$content \n")
        }

    fun getContent(filePathName: String, filter: String, filterType: FilterType): Single<List<String>> =
        Single.fromCallable {
            val file = File(filePathName)
            if (!file.exists()) throw InvalidParameterException("File $filePathName doesn't exists")
            if (filter.isEmpty()) file.readLines()
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