package data

import io.reactivex.rxjava3.core.Completable
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

    fun Single<Int>.synchronize(): Single<Int> =
        this.doOnSubscribe {
            semaphore.acquire()
        }.doFinally {
            semaphore.release()
        }
}