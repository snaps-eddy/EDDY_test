package com.snaps.common.utils.ui;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

public class UI {
	private static final String TAG = UI.class.getSimpleName();
	private static Handler shared_handler = null;
	
	public static Handler getHandler() {
		return shared_handler;
	}

	public static boolean isMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
	
	public static void assertInMainThread() {
		if (!isMainThread())
			throw new RuntimeException("Main thread assertion failed");
	}
	
	public static void recycleBitmap(Bitmap bm) {
		if (bm != null) {
			if (bm.isRecycled())
				Dlog.d("recycleBitmap() Bitmap.isRecycled()");
			else
				bm.recycle();
		}
	}
	
	public static <T> T callInMainThread(Callable<T> call) throws Exception {
		if (isMainThread())
			return call.call();
		else {
			FutureTask<T> task = new FutureTask<T>(call);
			getHandler().post(task);
			return task.get();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T findViewById(View parent, int id) {
		return (T)parent.findViewById(id);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T findViewById(Activity activity, int id) {
		return (T)activity.findViewById(id);
	}
}
