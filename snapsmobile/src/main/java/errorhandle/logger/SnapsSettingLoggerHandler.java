package errorhandle.logger;

import com.snaps.common.utils.ui.ISnapsLoggerBridge;

/**
 * Created by ysjeong on 2017. 11. 20..
 */

public class SnapsSettingLoggerHandler implements ISnapsLoggerBridge {

    public static SnapsSettingLoggerHandler createLoggerBridgeHandler() {
        return new SnapsSettingLoggerHandler();
    }

    @Override
    public void sendTextLog(String where, String log) {
        SnapsLogger.sendTextLog(where, log);
    }

    @Override
    public void appendTextLog(String where, String log) {
        SnapsLogger.appendTextLog(where, log);
    }
}
