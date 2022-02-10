package errorhandle.logger.model.factories;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsLoggerBase;
import errorhandle.logger.model.SnapsSchemeUrlLogger;

/**
 * Created by ysjeong on 2017. 11. 22..
 */
public class SnapsSchemeUrlLoggerCreator {
    public static SnapsLoggerBase createLogger(SnapsLoggerAttribute attribute) {
        return new SnapsSchemeUrlLogger(attribute);
    }
}
