package errorhandle.logger.model.factories;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsLoggerBase;
import errorhandle.logger.model.SnapsSimpleTextLogger;

/**
 * Created by ysjeong on 2017. 11. 22..
 */
public class SnapsSimpleTextLoggerCreator {
    public static SnapsLoggerBase createLogger(SnapsLoggerAttribute attribute) {
        return new SnapsSimpleTextLogger(attribute);
    }
}
