package com.snaps.common.utils.ui;

import java.util.concurrent.atomic.AtomicInteger;

import errorhandle.logger.Logg;

import android.annotation.SuppressLint;

import com.snaps.common.utils.log.Dlog;

public class ViewIDGenerator {
	private static final String TAG = ViewIDGenerator.class.getSimpleName();
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	@SuppressLint("NewApi")
	synchronized public static int generateViewId(int controlID) {

		if (controlID != -1) {
			return controlID;
		}

		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				Dlog.d("generateViewId() controlID:" + controlID + ", return:" + result);
				return result;
			}
		}

	}

}
