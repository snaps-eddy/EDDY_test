package com.snaps.mobile.domain.usecase

import io.reactivex.rxjava3.core.Flowable

interface FlowableUseCase<Type, in Params> {

    operator fun invoke(params: Params): Flowable<Type>

}