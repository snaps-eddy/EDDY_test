package errorhandle.logger.model;

import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public abstract class SnapsLoggerBase extends Exception {
    private static final String TAG = SnapsLoggerBase.class.getSimpleName();
    private static final long serialVersionUID = 5370095820500213884L;
    protected StringBuilder logBuilder = null;
    private SnapsLoggerAttribute loggerAttribute = null;

    public SnapsLoggerBase(SnapsLoggerAttribute attribute) {
        super(attribute != null ? attribute.getContents() : "");
        this.loggerAttribute = attribute;
    }

    public SnapsLoggerBase(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        try {
            return SnapsLogger.getStandardLogInfo();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return getLog();
        }
    }

    public SnapsLoggerAttribute getLoggerAttribute() {
        return loggerAttribute;
    }

    public String getLog() {
        return logBuilder != null ? logBuilder.toString() : super.getMessage();
    }

    public void appendLog(SnapsLoggerAttribute attribute) {
        if (attribute == null) return;

        if (logBuilder == null || attribute.isInitializeLog()) {
            attribute.setInitializeLog(false);
            logBuilder = new StringBuilder();
        }
        logBuilder.append(getLogContents(attribute)).append(getSeparator());

        handleLogAfterLogAppend();
    }

    protected void handleLogAfterLogAppend() { /** Hook */ }

    public void setLog(String log) {
        logBuilder = new StringBuilder();
        logBuilder.append(log);
    }

    protected void appendLog(String log) {
        if (logBuilder == null)
            logBuilder = new StringBuilder();
        logBuilder.append(log).append(getSeparator());

        handleLogAfterLogAppend();
    }

    protected String getSeparator() {
        return "\n";
    }

    protected String getLogContents(SnapsLoggerAttribute attribute) {
        return attribute != null ? attribute.getContents() : "";
    }
}
