package com.snaps.mobile.presentation.editor.utils

import com.snaps.mobile.domain.error.Reason
import com.snaps.mobile.domain.error.SnapsThrowable

fun Throwable.handleSnaps(): Reason? {
    return (this as? SnapsThrowable)?.reason
}