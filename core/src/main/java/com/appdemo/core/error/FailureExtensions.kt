package com.appdemo.core.error

fun Failure.toThrowable(): Throwable = when (this) {
    is Failure.NetworkError -> exception
    is Failure.ServerError -> Exception("Server error: $code ${message ?: ""}")
    is Failure.UnknownError -> throwable
}