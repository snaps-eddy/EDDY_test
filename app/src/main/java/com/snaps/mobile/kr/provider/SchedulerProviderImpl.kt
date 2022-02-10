package com.snaps.mobile.kr.provider

import com.snaps.common.android_utils.SchedulerProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject

class SchedulerProviderImpl @Inject constructor() : SchedulerProvider {

    private val uploadThreadPool = Executors.newFixedThreadPool(3)

    override val io: Scheduler = Schedulers.io()

    override val computation: Scheduler = Schedulers.computation()

    override val trampoline: Scheduler = Schedulers.trampoline()

    override val ui: Scheduler = AndroidSchedulers.mainThread()

    override val upload: Scheduler = Schedulers.from(uploadThreadPool)

}