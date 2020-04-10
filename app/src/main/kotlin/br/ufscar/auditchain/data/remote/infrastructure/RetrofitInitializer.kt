package br.ufscar.auditchain.data.remote.infrastructure

import br.ufscar.auditchain.common.Config
import br.ufscar.auditchain.data.remote.ElasticRemoteDataSource
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitInitializer {

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(
            BasicAuthInterceptor(
                user = Config.elasticUser,
                password = Config.elasticPwds
            )
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .client(httpClient)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Config.elasticHost)
        .build()

    fun createElasticService(): ElasticRemoteDataSource = retrofit.create(
        ElasticRemoteDataSource::class.java)

}