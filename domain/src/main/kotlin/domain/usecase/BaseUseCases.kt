package domain.usecase

import domain.utility.printExceptionLogInFile
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

abstract class BaseUseCase

abstract class ObservableUseCase<Response, in Request> : BaseUseCase() {
    internal abstract fun getRawObservable(request: Request): Observable<Response>
    fun getObservable(request: Request): Observable<Response> = getRawObservable(request).doOnError {
        printExceptionLogInFile(this::class.simpleName ?: "", it)
    }
}

abstract class SingleUseCase<Response, in Request> : BaseUseCase() {
    internal abstract fun getRawSingle(request: Request): Single<Response>
    fun getSingle(request: Request): Single<Response> = getRawSingle(request).doOnError {
        printExceptionLogInFile(this::class.simpleName ?: "", it)
    }
}

abstract class CompletableUseCase<in Request> : BaseUseCase() {
    internal abstract fun getRawCompletable(request: Request): Completable
    fun getCompletable(request: Request): Completable = getRawCompletable(request).doOnError {
        printExceptionLogInFile(this::class.simpleName ?: "", it)
    }
}