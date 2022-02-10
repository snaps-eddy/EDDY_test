package com.snaps.common.utils.ui;

public class CrashlyticsBridge {

	private static volatile CrashlyticsBridge instance;

	private ISnapsLoggerBridge loggerBridge = null;

	public static CrashlyticsBridge getInstance() {
		return instance;
	}

	public static void createInstance(ISnapsLoggerBridge loggerBridge) {
		if(instance == null) {
			synchronized(CrashlyticsBridge.class) {
				if(instance == null) {
					instance = new CrashlyticsBridge(loggerBridge);
				}
			}
		}
	}

	private CrashlyticsBridge(ISnapsLoggerBridge loggerBridge) {
		this.loggerBridge = loggerBridge;
    }

    public static void finalizeInstance() {
        if (instance != null) {
			instance.loggerBridge = null;
		}
    }

    public void sendTextLog(String where, String log) {
		if (loggerBridge != null)
			loggerBridge.sendTextLog(where, log);
	}

	public void appendTextLog(String where, String log) {
		if (loggerBridge != null)
			loggerBridge.appendTextLog(where, log);
	}
}