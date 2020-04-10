package br.ufscar.auditchain.data.io.infrastructure

import br.ufscar.auditchain.common.Config
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class TextIO(
    private val filePath: String = Config.filePath,
    private val scheduler: Scheduler = Schedulers.io()
) {

    fun upsertLine(fileName: String, content: String): Completable {
        return Completable.fromAction {
            TextStorage().upsertLine("${filePath}$fileName", content)
        }.subscribeOn(scheduler)
    }

    fun getContent(
        fileName: String,
        filter: String = "",
        filterType: TextStorage.FilterType = TextStorage.FilterType.START_WITH
    ): Single<List<String>> {
        return Single.fromCallable {
            TextStorage().getContent("${filePath}$fileName", filter, filterType)
        }.subscribeOn(scheduler)
    }

}