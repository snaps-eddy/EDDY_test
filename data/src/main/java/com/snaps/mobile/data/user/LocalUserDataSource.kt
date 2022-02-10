package com.snaps.mobile.data.user

import android.content.SharedPreferences
import com.snaps.common.utils.constant.Const_VALUE
import com.snaps.mobile.domain.user.User
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LocalUserDataSource @Inject constructor(
    private val sharedPreference: SharedPreferences
) {
    fun getUser(userNo: String): Single<User> {
        return Single.fromCallable {
            sharedPreference.getString(Const_VALUE.KEY_SNAPS_USER_ID, "")?.run { User(userNo) }
                ?: throw NoUserException("Not found local user.")
        }
    }

    class NoUserException(msg: String) : IllegalStateException(msg)

}