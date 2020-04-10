package br.ufscar.auditchain.data.io.infrastructure

import br.ufscar.auditchain.common.Config
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.Serializable

class ObjectIO(
    private val filePath: String = Config.filePath,
    private val scheduler: Scheduler = Schedulers.io()
) {

    fun <T : Serializable> write(fileName: String, obj: T): Completable {
        return Completable.fromAction {
            ObjectStorage().writeObject("${filePath}$fileName", obj)
        }.subscribeOn(scheduler)
    }

    fun <T : Serializable> read(fileName: String): Single<T> {
        return Single.fromCallable {
            ObjectStorage().readObject<T>("${filePath}$fileName")
        }.subscribeOn(scheduler)
    }

}