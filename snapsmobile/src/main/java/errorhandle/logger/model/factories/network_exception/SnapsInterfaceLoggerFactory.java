package errorhandle.logger.model.factories.network_exception;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsLoggerBase;
import errorhandle.logger.model.factories.SnapsInterfaceLoggerCreator;

/**
 * Created by ysjeong on 2017. 12. 1..
 */

public class SnapsInterfaceLoggerFactory {
    public static SnapsLoggerBase createInterfaceDetailException(SnapsLoggerAttribute attribute) {
        if (attribute == null || attribute.getContents() == null) return SnapsInterfaceLoggerCreator.createLogger(attribute);

        String msg = attribute.getContents();
        if (msg.contains("UnknownHostException")) {
            return SnapsInterfaceUnknownHostExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("SocketException")) {
            return SnapsInterfaceSocketExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("Timeout") || msg.contains("timed out") || msg.contains("time out") || msg.contains("ETIMEDOUT")) {
            return SnapsInterfaceTimeoutExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("ParseException")) {
            return SnapsInterfaceParseExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("HttpHostConnectException")) {
            return SnapsInterfaceHostConnectExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("SSLPeerUnverifiedException")) {
            return SnapsInterfaceSSLExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("502 Bad Gateway")) {
            return SnapsInterfaceBadGatewayExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("FileNotFoundException")) {
            return SnapsInterfaceFileNotFoundExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("SAXException")) {
            return SnapsInterfaceSaxExceptionLoggerCreator.createLogger(attribute);
        } else if (msg.contains("ClientProtocolException")) {
            return SnapsInterfaceClientProtocolExceptionLoggerCreator.createLogger(attribute);
        }

        return SnapsInterfaceLoggerCreator.createLogger(attribute);
    }
}
