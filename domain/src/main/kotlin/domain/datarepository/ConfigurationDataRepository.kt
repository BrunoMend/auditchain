package domain.datarepository

import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration
import io.reactivex.rxjava3.core.Single

interface ConfigurationDataRepository {
    fun getElasticsearchConfiguration(): Single<ElasticsearchConfiguration>
    fun getAttestationConfiguration(): Single<AttestationConfiguration>
}