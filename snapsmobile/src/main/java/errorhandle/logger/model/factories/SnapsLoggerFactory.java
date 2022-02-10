package errorhandle.logger.model.factories;

import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderExceptionFactory;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsLoggerBase;
import errorhandle.logger.model.factories.network_exception.SnapsInterfaceLoggerFactory;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public class SnapsLoggerFactory {
    /**
     * 클래스를 바로 생성 안하고 아래처럼 Creator로 감싸는 이유는 Crashlytics에 생성한 클래스가 기록되기 때문이다..
     */
    public static SnapsLoggerBase createLoggerWithLoggerAttribute(SnapsLoggerAttribute attribute) {
        if (attribute == null) return null;

        switch (attribute.getLogType()) {
            case CLASS_TRACKING:
                return SnapsClassLoggerCreator.createLogger(attribute);
            case TEXT:
                return SnapsSimpleTextLoggerCreator.createLogger(attribute);
            case INTERFACE:
                return SnapsInterfaceLoggerFactory.createInterfaceDetailException(attribute);
            case EXCEPTION:
                return SnapsExceptionLoggerCreator.createLogger(attribute);
            case SNAPS_SCHEME_URL:
                return SnapsSchemeUrlLoggerCreator.createLogger(attribute);
            case ORDER:
                return SnapsOrderExceptionFactory.createSnapsOrderExceptionWithDetailMsg(attribute.getOrderType(), attribute.getContents());

        }
        return null;
    }
}
