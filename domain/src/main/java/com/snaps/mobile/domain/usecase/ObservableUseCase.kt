package com.snaps.mobile.domain.usecase

import io.reactivex.rxjava3.core.Observable

interface ObservableUseCase<Type, in Params> {

    operator fun invoke(params: Params): Observable<Type>

}