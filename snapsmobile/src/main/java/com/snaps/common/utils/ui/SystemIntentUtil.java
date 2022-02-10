package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 *
 */
public class SystemIntentUtil {
	public static void showSystemSetting ( Activity act ) {

		String packageName = "com.snaps.mobile.kr";
		if (com.snaps.common.utils.constant.Config.isDevelopVersion()) {
			packageName = "com.snaps.mobile.kr.develop";
		}

		Intent intent = new Intent();
		String action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
		Uri data = Uri.parse( "package:" + packageName );
		intent.setAction( action );
		intent.setData( data );
		act.startActivity(intent);
	}
}
