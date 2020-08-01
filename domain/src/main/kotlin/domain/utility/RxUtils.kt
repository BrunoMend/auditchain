package domain.utility

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.Semaphore

fun Completable.synchronize(semaphore: Semaphore): Completable =
    this.doOnSubscribe {
        semaphore.acquire()
    }.doFinally {
        semaphore.release()
    }

fun <T> Single<T>.synchronize(semaphore: Semaphore): Single<T> =
    this.doOnSubscribe {
        semaphore.acquire()
    }.doFinally {
        semaphore.release()
    }

fun <T> Observable<T>.synchronize(semaphore: Semaphore): Observable<T> =
    this.doOnSubscribe {
        semaphore.acquire()
    }.doFinally {
        semaphore.release()
    }