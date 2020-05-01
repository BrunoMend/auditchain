package data.file.infrastructure

import domain.di.IOScheduler
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.io.*
import javax.inject.Inject

class ObjectStorage @Inject constructor(@IOScheduler private val ioScheduler: Scheduler) {

    fun <T : Serializable> writeObject(path: String, fileName: String, obj: T): Completable =
        Completable.fromAction {
            val file = File(path)
            file.mkdirs()
            val fileOutputStream = FileOutputStream("$path$fileName")
            val objectOut = ObjectOutputStream(fileOutputStream)
            objectOut.writeObject(obj)
            objectOut.close()
            fileOutputStream.close()
        }.subscribeOn(ioScheduler)

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable> readObject(filePathName: String): Single<T> =
        Single.fromCallable {
            val fileInputStream = FileInputStream(filePathName)
            val objectIn = ObjectInputStream(fileInputStream)
            val result: T = objectIn.readObject() as? T ?: throw NullPointerException()
            objectIn.close()
            fileInputStream.close()
            result
        }.subscribeOn(ioScheduler)

}