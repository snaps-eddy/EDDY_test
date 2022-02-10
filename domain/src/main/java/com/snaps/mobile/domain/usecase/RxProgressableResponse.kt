package com.snaps.mobile.domain.usecase

data class RxProgressableResponse<T>(
    val index: Int,
    val totalCount: Int,
    val value: T
) {
}