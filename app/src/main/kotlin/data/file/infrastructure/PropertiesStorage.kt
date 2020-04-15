package data.file.infrastructure

import io.reactivex.rxjava3.core.Single
import java.io.FileInputStream
import java.util.*

class PropertiesStorage {
    fun getProperties(filePath: String): Single<Properties> =
        Single.fromCallable {
            val properties = Properties()
            properties.load(FileInputStream(filePath))
            properties
        }
}