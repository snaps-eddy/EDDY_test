package com.snaps.mobile.domain.error

/**
 * 로직상 에러등을 처리하기 위한 Wrapper 클래스
 */
class SnapsThrowable(val reason: Reason) : Throwable(message = reason.message) {

    constructor(message: String) : this(Reason.SimpleMessage(message))

}