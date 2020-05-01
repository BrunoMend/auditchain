package data.repository

import data.file.ObjectFileDataSource
import domain.datarepository.FileDataRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.Serializable
import javax.inject.Inject

class FileRepository @Inject constructor(private val objectFileDataSource: ObjectFileDataSource) : FileDataRepository {
    override fun <T : Serializable> saveObject(path: String, fileName: String, obj: T): Completable =
        objectFileDataSource.write(path, fileName, obj)

    override fun <T : Serializable> getObject(path: String, fileName: String): Single<T> =
        objectFileDataSource.read(path, fileName)
}