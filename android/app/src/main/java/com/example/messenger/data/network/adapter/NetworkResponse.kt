package com.example.messenger.data.network.adapter

sealed class NetworkResponse<out T : Any, out U : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T) : NetworkResponse<T, Nothing>()

    /**
     * Failure response with body
     */
    data class ApiError<U : Any>(val body: U?, val code: Int, val error: Throwable? = null) : NetworkResponse<Nothing, U>()
}