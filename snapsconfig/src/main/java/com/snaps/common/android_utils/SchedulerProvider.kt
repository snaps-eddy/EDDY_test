package com.snaps.common.android_utils

import io.reactivex.rxjava3.core.Scheduler

interface SchedulerProvider {

    val io: Scheduler
    val computation: Scheduler
    val trampoline: Scheduler
    val ui: Scheduler
    val upload: Scheduler

}