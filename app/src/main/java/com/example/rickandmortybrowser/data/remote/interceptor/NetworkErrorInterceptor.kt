package com.example.rickandmortybrowser.data.remote.interceptor

import com.example.rickandmortybrowser.util.AppConstants
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class NetworkErrorInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (_: SocketTimeoutException) {
            chain.timeoutResponse(
                code = AppConstants.TIMEOUT_HTTP_STATUS_CODE,
                message = "Network timeout",
            )
        } catch (_: TimeoutException) {
            chain.timeoutResponse(
                code = AppConstants.TIMEOUT_HTTP_STATUS_CODE,
                message = "Request timeout",
            )
        } catch (_: IOException) {
            chain.timeoutResponse(
                code = AppConstants.NETWORK_ERROR_HTTP_STATUS_CODE,
                message = "Network unavailable",
            )
        }
    }
}

private fun Interceptor.Chain.timeoutResponse(code: Int, message: String): Response {
    return Response.Builder()
        .request(request())
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message(message)
        .body(
            AppConstants.EMPTY_ERROR_RESPONSE_BODY
                .toResponseBody("application/json".toMediaType()),
        )
        .build()
}
