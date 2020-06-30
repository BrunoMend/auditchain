package data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.Semaphore

abstract class SemaphoreDataSource {
    private var semaphore: Semaphore = Semaphore(1, true)

    fun Completable.synchronize(): Completable =
        this.doOnSubscribe {
            semaphore.acquire()
        }.doFinally {
            semaphore.release()
        }

    fun <T> Single<T>.synchronize(): Single<T> =
        this.doOnSubscribe {
            semaphore.acquire()
        }.doFinally {
            semaphore.release()
        }

    fun <T> Observable<T>.synchronize(): Observable<T> =
        this.doOnSubscribe {
            semaphore.acquire()
        }.doFinally {
            semaphore.release()
        }
}