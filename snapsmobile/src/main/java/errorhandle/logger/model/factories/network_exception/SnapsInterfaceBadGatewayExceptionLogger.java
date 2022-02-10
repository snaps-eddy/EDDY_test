package errorhandle.logger.model.factories.network_exception;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsInterfaceLogger;

/**
 * Created by ysjeong on 2017. 11. 22..
 */
public class SnapsInterfaceBadGatewayExceptionLogger extends SnapsInterfaceLogger {
    private static final long serialVersionUID = 8191934368323154237L;
    private final int MAX_LENGTH_LOG_STRING = 10240;

    public SnapsInterfaceBadGatewayExceptionLogger(SnapsLoggerAttribute attribute) {
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
