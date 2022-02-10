package errorhandle.logger;

import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public class SnapsLoggerAttribute {
    public enum LOG_TYPE {
        CLASS_TRACKING,
        TEXT,
        ORDER,
        EXCEPTION,
        INTERFACE,
        SNAPS_SCHEME_URL,
        DEBUG
    }

    private LOG_TYPE logType;
    private String contents = "";
    private SnapsOrderConstants.eSnapsOrderType orderType = null;
    private boolean isInitializeLog = false;

    public LOG_TYPE getLogType() {
        return logType;
    }

    public String getContents() {
        return contents;
    }

    public void appendContents(String newContents) {
        this.contents += newContents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public SnapsOrderConstants.eSnapsOrderType getOrderType() {
        return orderType;
    }

    public boolean isInitializeLog() {
        return isInitializeLog;
    }

    public void setInitializeLog(boolean initializeLog) {
        isInitializeLog = initializeLog;
    }

    private SnapsLoggerAttribute(Builder builder) {
        logType = builder.logType;
        contents = builder.contents;
        orderType = builder.orderType;
        isInitializeLog = builder.isInitializeLog;
    }

    public static class Builder {
        private LOG_TYPE logType;
        private String contents = "";
        private SnapsOrderConstants.eSnapsOrderType orderType = null;
        private boolean isInitializeLog = false;

        public Builder setInitializeLog(boolean initializeLog) {
            isInitializeLog = initializeLog;
            return this;
        }

        public Builder setLogType(LOG_TYPE logType) {
            this.logType = logType;
            return this;
        }

        public Builder setContents(String contents) {
            this.contents = contents;
            return this;
        }

        public Builder setOrderType(SnapsOrderConstants.eSnapsOrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public SnapsLoggerAttribute create() {
            return new SnapsLoggerAttribute(this);
        }
    }
}
