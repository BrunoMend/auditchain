package common.di

import dagger.Module
import dagger.Provides
import data.remote.ElasticsearchRemoteDataSource
import data.remote.infrastructure.BasicAuthInterceptor
import data.repository.ConfigurationRepository
import data.repository.ElasticsearchRepository
import data.repository.FileRepository
import data.repository.TimestampRepository
import domain.datarepository.ConfigurationDataRepository
import domain.datarepository.ElasticsearchDataRepository
import domain.datarepository.FileDataRepository
import domain.datarepository.TimestampDataRepository
import domain.di.ComputationScheduler
import domain.di.IOScheduler
import domain.model.AttestationConfiguration
import domain.model.ElasticsearchConfiguration
import domain.utility.Logger
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @IOScheduler
    fun ioScheduler(): Scheduler = Schedulers.io()

    @Provides
    @ComputationScheduler
    fun computationScheduler(): Scheduler = Schedulers.computation()

    @Provides
    @Singleton
    fun logger() = object : Logger {
        override fun log(msg: String) {
            println(msg)
        }
    }

    @Provides
    @Singleton
    fun elasticsearchConfiguration(configurationRepository: ConfigurationDataRepository): ElasticsearchConfiguration =
        configurationRepository.getElasticsearchConfiguration().blockingGet()

    @Provides
    @Singleton
    fun attestationConfiguration(configurationRepository: ConfigurationDataRepository): AttestationConfiguration =
        configurationRepository.getAttestationConfiguration().blockingGet()

    @Provides
    @Singleton
    fun elasticsearchHttpClient(elasticsearchConfiguration: ElasticsearchConfiguration): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                BasicAuthInterceptor(
                    user = elasticsearchConfiguration.elasticUser,
                    password = elasticsearchConfiguration.elasticPwds
                )
            ).build()

    @Provides
    @Singleton
    fun elasticsearchRetrofit(
        elasticsearchConfiguration: ElasticsearchConfiguration,
        elasticsearchHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .client(elasticsearchHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(elasticsearchConfiguration.elasticHost)
            .build()


    @Provides
    fun elasticsearchRemoteDataSource(retrofit: Retrofit): ElasticsearchRemoteDataSource =
        retrofit.create(ElasticsearchRemoteDataSource::class.java)

    @Provides
    @Singleton
    fun configurationDataRepository(configurationRepository: ConfigurationRepository)
            : ConfigurationDataRepository = configurationRepository

    @Provides
    fun elasticsearchDataRepository(elasticsearchRepository: ElasticsearchRepository)
            : ElasticsearchDataRepository = elasticsearchRepository

    @Provides
    @Singleton
    fun timestampDataRepository(timestampRepository: TimestampRepository)
            : TimestampDataRepository = timestampRepository

    @Provides
    @Singleton
    fun fileDataRepository(fileRepository: FileRepository)
            : FileDataRepository = fileRepository
}