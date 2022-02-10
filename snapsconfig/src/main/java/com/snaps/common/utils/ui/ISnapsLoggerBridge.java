package com.snaps.common.utils.ui;

/**
 * Created by ysjeong on 2017. 11. 27..
 */

public interface ISnapsLoggerBridge {
    void sendTextLog(String where, String log);
    void appendTextLog(String where, String log);
}
