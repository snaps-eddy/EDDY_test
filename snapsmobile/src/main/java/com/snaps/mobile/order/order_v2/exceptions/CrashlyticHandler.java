package com.snaps.mobile.order.order_v2.exceptions;

import android.content.Context;

import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.ICrashlytics;
import com.snaps.mobile.activity.home.HomeActivity;
import com.snaps.mobile.activity.home.RenewalHomeActivity;

import errorhandle.logger.SnapsLogger;

public class CrashlyticHandler extends ICrashlytics {

	private boolean isAppFinishedByCrash = false;

	public CrashlyticHandler() {
		isAppFinishedByCrash = false;
	}

	@Override
	public void postThrowable(Throwable t) {
		if (t == null) {
			return;
		}
		SnapsLogger.sendExceptionLogWithLog("crash", t.toString());
	}

	@Override
	public boolean isAppFinishedByCrash() {
		return isAppFinishedByCrash;
	}

	@Override
	public void setAppFinishedByCrash() {
		isAppFinishedByCrash = true;
	}

	@Override
	public void forceAppFinish() {
		Context subContext = ContextUtil.getSubContext();
		if (subContext != null) {
			if(subContext instanceof HomeActivity) {
				((HomeActivity) subContext).forceAppFinish();
			} else if(subContext instanceof RenewalHomeActivity) {
				((RenewalHomeActivity) subContext).forceAppFinish();
			}
		}
	}
}
