package errorhandle.logger.model;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public class SnapsDebugLogger extends SnapsLoggerBase {

    private static final long serialVersionUID = 2712641702654647098L;

    public static SnapsDebugLogger createLoggerWithAttribute(SnapsLoggerAttribute attribute) {
        return new SnapsDebugLogger(attribute);
    }

    public SnapsDebugLogger(SnapsLoggerAttribute attribute) {
        super(attribute);
    }
}
