package errorhandle.logger.model;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public class SnapsClassLogger extends SnapsLoggerBase {
    private static final long serialVersionUID = -4089655897063145277L;
    private final int MAX_LENGTH_LOG_STRING = 4096;

    public SnapsClassLogger(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    @Override
    protected String getSeparator() {
        return "->";
    }

    @Override
    protected String getLogContents(SnapsLoggerAttribute attribute) {
        String name = attribute != null ? attribute.getContents() : "";
        if (name != null && name.contains(".")) {
            name = name.substring((name.lastIndexOf(".")+1));
        }
        return name;
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
