package com.snaps.mobile.data.util

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.kotlin.Flowables
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

internal typealias RETRY_PREDICATE = (Throwable) -> Boolean

internal const val MAX_RETRIES = 3L
internal const val DEFAULT_INTERVAL = 2L

internal val TIMEOUT: RETRY_PREDICATE = { it is SocketTimeoutException }
internal val NETWORK: RETRY_PREDICATE = { it is IOException }
internal val SERVICE_UNAVAILABLE: RETRY_PREDICATE = { it is HttpException && it.code() == 503 }
internal val SERVICE_TEMPOLARY_UNAVAILABLE: RETRY_PREDICATE = { it is HttpException && it.code() == 500 }

internal fun <T> applyRetryPolicy(
    vararg predicates: RETRY_PREDICATE,
    maxRetries: Long = MAX_RETRIES,
    interval: Long = DEFAULT_INTERVAL,
    unit: TimeUnit = TimeUnit.SECONDS,
) = SingleTransformer<T, T> { single ->
    single.retryWhen { attempts ->
        Flowables.zip(
            attempts.map { error -> if (predicates.count { it(error) } > 0) error else throw error },
            Flowable.interval(interval, unit)
        ).map { (error, retryCount) ->
            if (retryCount >= maxRetries - 1) throw error
        }
    }.onErrorResumeNext { Single.error(it) }
}
