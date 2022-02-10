package com.snaps.mobile.data.user

import com.snaps.mobile.domain.user.User
import com.snaps.mobile.domain.user.UserRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val local: LocalUserDataSource
) : UserRepository {

    override fun getUser(userNo: String): Single<User> {
        return local.getUser(userNo)
    }

}