package com.snaps.mobile.domain


private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

val randomString: String
    get() = (1..10)
        .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")