package data.file

import data.file.infrastructure.ObjectStorage
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.Serializable
import javax.inject.Inject

class ObjectFileDataSource @Inject constructor(private val objectStorage: ObjectStorage) {

    fun <T : Serializable> write(path: String, fileName: String, obj: T): Completable =
        objectStorage.writeObject("$path$fileName", obj)

    fun <T : Serializable> read(path: String, fileName: String): Single<T> =
        objectStorage.readObject<T>("$path$fileName")
}