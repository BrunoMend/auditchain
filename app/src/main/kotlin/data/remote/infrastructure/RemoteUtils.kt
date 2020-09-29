package data.remote.infrastructure

import domain.exception.HttpClientException
import domain.exception.NoInternetException
import domain.exception.HttpServerException
import retrofit2.HttpException
import java.net.UnknownHostException

fun Throwable.toExpectedRemoteException(): Throwable =
    when(this) {
        is HttpException ->
            when(code()) {
                in 400..499 -> HttpClientException(code())
                in 500..599 -> HttpServerException(code())
                else -> this
            }
        is UnknownHostException -> NoInternetException()
        else -> this
    }