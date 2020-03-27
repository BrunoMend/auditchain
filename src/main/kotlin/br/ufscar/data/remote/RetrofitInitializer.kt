package br.ufscar.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitInitializer {

    private val httpClient = OkHttpClient.Builder()
            //TODO add user and password
            //TODO get user data from config file
        .addInterceptor(BasicAuthInterceptor(user=,password=))
        .build()

    private val retrofit = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("https://elasticsearch.sin.ufscar.br/")
        .build()

    fun createElasticService(): RemoteDataSource = retrofit.create(RemoteDataSource::class.java)

}