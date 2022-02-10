package com.snaps.mobile.data.util

import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.error.SnapsThrowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import retrofit2.HttpException

fun <T> handleHttpError(): SingleTransformer<T, T> = SingleTransformer { upstream ->
    upstream.onErrorResumeNext { throwable ->
        when (throwable) {
            is HttpException -> {
                when (val httpErrorCode = throwable.code()) {
                    500 -> {
                        Single.error(SnapsThrowable(Reason.HttpServiceUnavailable))
                    }
                    else -> {
                        Dlog.d("UnHandle http error code $httpErrorCode")
                        Single.error(throwable)
                    }
                }
            }
            else -> {
                Single.error(throwable)
            }
        }
    }
}