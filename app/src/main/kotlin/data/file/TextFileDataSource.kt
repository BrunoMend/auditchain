package data.file

import data.file.infrastructure.TextStorage
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

class TextFileDataSource(private val textStorage: TextStorage, private val scheduler: Scheduler) {

    fun upsertLine(path: String, fileName: String, content: String): Completable =
        textStorage.insertLine("$path$fileName", content)

    fun getContent(
        path: String, fileName: String, filter: String = "",
        filterType: TextStorage.FilterType = TextStorage.FilterType.START_WITH
    ): Single<List<String>> =
        textStorage.getContent("$path$fileName", filter, filterType)

}