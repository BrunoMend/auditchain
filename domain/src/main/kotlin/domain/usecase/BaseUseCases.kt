package domain.usecase

import domain.exception.log
import domain.utility.getFileLogger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.logging.Logger

abstract class BaseUseCase {
    companion object {
        @JvmStatic
        protected val logger: Logger = getFileLogger(this::class.simpleName ?: "")
    }
}

abstract class ObservableUseCase<Response, in Request> : BaseUseCase() {
    internal abstract fun getRawObservable(request: Request): Observable<Response>
    fun getObservable(request: Request): Observable<Response> = getRawObservable(request).doOnError { it.log(logger) }
}

abstract class SingleUseCase<Response, in Request> : BaseUseCase() {
    internal abstract fun getRawSingle(request: Request): Single<Response>
    fun getSingle(request: Request): Single<Response> = getRawSingle(request).doOnError { it.log(logger) }
}

abstract class CompletableUseCase<in Request> : BaseUseCase() {
    internal abstract fun getRawCompletable(request: Request): Completable
    fun getCompletable(request: Request): Completable = getRawCompletable(request).doOnError { it.log(logger) }
}