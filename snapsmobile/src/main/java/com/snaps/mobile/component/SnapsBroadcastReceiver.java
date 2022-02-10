package com.snaps.mobile.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

public class SnapsBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = SnapsBroadcastReceiver.class.getSimpleName();
	ImpSnapsBroadcastReceiver impRecevice = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Dlog.d("onReceive() intent:" + intent);
		if (intent != null && intent.getDataString() == null) {
			if (impRecevice != null)
				impRecevice.onReceiveData(context, intent);
		}
	}

	public void setImpRecevice(ImpSnapsBroadcastReceiver impRecevice) {
		this.impRecevice = impRecevice;
	}

	public interface ImpSnapsBroadcastReceiver {
		void onReceiveData(Context context, Intent intent);
	}

}
