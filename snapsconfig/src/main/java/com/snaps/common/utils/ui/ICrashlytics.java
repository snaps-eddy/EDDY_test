package com.snaps.common.utils.ui;

public abstract class ICrashlytics {
	public abstract void postThrowable(Throwable t);

	public abstract boolean isAppFinishedByCrash();

	public abstract void setAppFinishedByCrash();

	public abstract void forceAppFinish();
}
