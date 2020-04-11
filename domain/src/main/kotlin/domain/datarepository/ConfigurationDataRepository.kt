package domain.datarepository

import domain.model.Configuration
import io.reactivex.rxjava3.core.Single

interface ConfigurationDataRepository {
    fun getConfiguration(): Single<Configuration>
}