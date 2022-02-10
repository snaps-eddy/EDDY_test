package com.snaps.mobile.kr

import com.snaps.common.HomeActivity
import com.snaps.mobile.activity.home.RenewalHomeActivity

class HomeActivityImpl : HomeActivity {

    override fun getTargetClass(): Class<*> = RenewalHomeActivity::class.java

}