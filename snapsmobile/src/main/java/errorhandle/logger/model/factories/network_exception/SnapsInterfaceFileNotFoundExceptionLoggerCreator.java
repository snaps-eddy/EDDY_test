package errorhandle.logger.model.factories.network_exception;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsLoggerBase;

/**
 * Created by ysjeong on 2017. 11. 22..
 */
public class SnapsInterfaceFileNotFoundExceptionLoggerCreator {
    public static SnapsLoggerBase createLogger(SnapsLoggerAttribute attribute) {
        return new SnapsInterfaceFileNotFoundExceptionLogger(attribute);
    }
}
