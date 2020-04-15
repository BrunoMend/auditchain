package data.remote.infrastructure

import data.remote.ElasticsearchRemoteDataSource
import domain.model.ElasticsearchConfiguration
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ElasticsearchRetrofitInitializer(elasticsearchConfiguration: ElasticsearchConfiguration) {

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(
            BasicAuthInterceptor(
                user = elasticsearchConfiguration.elasticUser,
                password = elasticsearchConfiguration.elasticPwds
            )
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .client(httpClient)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(elasticsearchConfiguration.elasticHost)
        .build()

    fun createElasticService(): ElasticsearchRemoteDataSource = retrofit.create(
        ElasticsearchRemoteDataSource::class.java
    )

}