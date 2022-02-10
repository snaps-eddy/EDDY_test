package com.snaps.mobile.utils.ui;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class ProcessUtil {

	/***
	 * 실행중인지를 판단 하는 함
	 * 
	 * @param context
	 * @param intent
	 */
	public boolean processList(Context context) {
		/* 실행중인 process 목록 보기 */

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appList = am.getRunningAppProcesses();

		boolean isRunning = false;

		for (RunningAppProcessInfo rap : appList) {
			if (rap.processName.equals("com.snaps.mobile.kr") && rap.importance != RunningAppProcessInfo.IMPORTANCE_SERVICE) {
				return true;
			}
		}
		return isRunning;
	}

}
