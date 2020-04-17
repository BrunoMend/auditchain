package data.remote.infrastructure

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor(val user: String, val password: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", Credentials.basic(user, password)).build()
        return chain.proceed(authenticatedRequest)
    }
}