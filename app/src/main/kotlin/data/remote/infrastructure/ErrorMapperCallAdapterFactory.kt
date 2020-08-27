package data.remote.infrastructure

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
open class ErrorMapperCallAdapterFactory @Inject constructor() : CallAdapter.Factory() {

    private val rxJavaCallAdapterFactory = RxJava3CallAdapterFactory.create()

    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<in Any, out Any> =
        ErrorMapperAdapterWrapper(rxJavaCallAdapterFactory.get(returnType, annotations, retrofit) as CallAdapter<in Any, out Any>)

    private class ErrorMapperAdapterWrapper(private val wrapped: CallAdapter<in Any, out Any>) : CallAdapter<Any, Single<Any>> {

        override fun responseType(): Type = wrapped.responseType()

        override fun adapt(call: Call<Any>): Single<Any> =
            (wrapped.adapt(call) as Single<Any>)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext {
                    Single.error(it.toExpectedRemoteException())
                }
    }
}