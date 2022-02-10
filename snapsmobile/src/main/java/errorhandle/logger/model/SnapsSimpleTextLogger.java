package errorhandle.logger.model;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public class SnapsSimpleTextLogger extends SnapsLoggerBase {
    private static final long serialVersionUID = -3333625273424175381L;

    private final int MAX_LENGTH_LOG_STRING = 20480;

    public static SnapsSimpleTextLogger createLoggerWithAttribute(SnapsLoggerAttribute attribute) {
        return new SnapsSimpleTextLogger(attribute);
    }

    public SnapsSimpleTextLogger(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    @Override
    protected void handleLogAfterLogAppend() {
        if (logBuilder != null && logBuilder.length() > MAX_LENGTH_LOG_STRING) { //너무 과대하게 로그가 쌓이는 걸 방지 하기 위해..
            String buffer = logBuilder.substring(logBuilder.length() - MAX_LENGTH_LOG_STRING);
            logBuilder.setLength(0);
            logBuilder.append("......").append(buffer);
        }
    }
}
