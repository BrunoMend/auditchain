package data.file

import data.file.infrastructure.TextStorage
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TextFileDataSource @Inject constructor(private val textStorage: TextStorage) {

    fun upsertLine(path: String, fileName: String, content: String): Completable =
        textStorage.insertLine("$path$fileName", content)

    fun getContent(
        path: String, fileName: String, filter: String = "",
        filterType: TextStorage.FilterType = TextStorage.FilterType.START_WITH
    ): Single<List<String>> =
        textStorage.getContent("$path$fileName", filter, filterType)

}