package br.ufscar.auditchain.data.remote

import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ElasticRemoteDataSource {

    //method GET must not have a request body.
    //https://github.com/square/okhttp/issues/3154

    //URI query
    //https://www.elastic.co/guide/en/elasticsearch/reference/current/search-uri-request.html
    //https://www.elastic.co/guide/en/elasticsearch/reference/7.6/query-dsl-query-string-query.html#query-string-syntax
    //https://stackoverflow.com/questions/14838567/range-query-in-elasticsearch-get-without-body

    @GET("/{indexPattern}/_search")
    fun getLogs(@Path("indexPattern") indexPattern: String,
                @Query("q") query: String,
                @Query("size") size: Int): Single<String>
}