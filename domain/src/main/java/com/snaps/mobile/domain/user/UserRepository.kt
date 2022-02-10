package com.snaps.mobile.domain.user

import io.reactivex.rxjava3.core.Single

interface UserRepository {

    fun getUser(userNo: String): Single<User>

}