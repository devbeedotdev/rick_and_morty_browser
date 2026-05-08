package com.example.rickandmortybrowser.data.repository

import com.example.rickandmortybrowser.util.AppConstants
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

internal fun Throwable.toRepositoryMessage(): String {
    return when (this) {
        is SocketTimeoutException -> AppConstants.NETWORK_TIMEOUT_MESSAGE
        is IOException -> AppConstants.NETWORK_ERROR_MESSAGE
        is HttpException -> message()
            ?.takeIf { it.isNotBlank() }
            ?: AppConstants.UNKNOWN_ERROR_MESSAGE
        else -> message?.takeIf { it.isNotBlank() } ?: AppConstants.UNKNOWN_ERROR_MESSAGE
    }
}
