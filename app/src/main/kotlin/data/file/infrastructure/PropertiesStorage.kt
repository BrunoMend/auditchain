package data.file.infrastructure

import domain.di.IOScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject

class PropertiesStorage @Inject constructor(@IOScheduler private val ioScheduler: Scheduler) {
    fun getProperties(filePath: String): Single<Properties> =
        Single.fromCallable {
            val properties = Properties()
            properties.load(FileInputStream(filePath))
            properties
        }.subscribeOn(ioScheduler)
}