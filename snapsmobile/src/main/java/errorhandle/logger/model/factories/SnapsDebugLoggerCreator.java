package errorhandle.logger.model.factories;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsDebugLogger;
import errorhandle.logger.model.SnapsLoggerBase;

/**
 * Created by ysjeong on 2017. 11. 22..
 */
public class SnapsDebugLoggerCreator {
    public static SnapsLoggerBase createLogger(SnapsLoggerAttribute attribute) {
        return new SnapsDebugLogger(attribute);
    }
}
