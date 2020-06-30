package domain.datarepository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.Serializable

@Deprecated("It doesn't make sense anymore")
interface FileDataRepository {
    fun <T : Serializable> saveObject(path: String, fileName: String, obj: T): Completable
    fun <T : Serializable> getObject(path: String, fileName: String): Single<T>
}