package com.snaps.mobile.domain.error

sealed class Reason {

    abstract val message: String

    data class OverMaxCount(
        val max: Int,
        val current: Int,
    ) : Reason() {
        override val message: String = "Over Max Count. max: $max, current: $current"
    }

    data class UnderMinCount(
        val min: Int,
        val current: Int
    ) : Reason() {
        override val message: String = "Under min count. min : $min, current : $current"
    }

    data class SimpleMessage(
        override val message: String
    ) : Reason()

    object HttpServiceUnavailable : Reason() {
        override val message: String = "Http Service Unavailable"
    }
}