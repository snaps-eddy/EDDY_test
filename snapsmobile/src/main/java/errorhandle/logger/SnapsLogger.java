package errorhandle.logger;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderExceptionFactory;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsNetworkExceptionCreator;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import errorhandle.logger.model.SnapsLoggerBase;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.model.factories.SnapsLoggerFactory;
import errorhandle.logger.model.factories.network_exception.SnapsInterfaceLoggerFactory;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.executor.WebLogSendExecutor;
import errorhandle.logger.web.request.WebLogRequestBuilder;
import errorhandle.logger.web.request.interfacies.WebLogRequestInfo;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public class SnapsLogger {
    private static final String TAG = SnapsLogger.class.getSimpleName();
    private static final int MAX_SAVE_MENU_CLICK_HISTORY_COUNT = 5;

    private static final long ALLOW_TIME_OF_CONSECUTIVE_LOG_EVENT_TRANSFER = 100; //동일한 로그가 연속적으로 날아가지 않게 하기 위해

    private static final String CUSTOM_LOG_FILE_PATH = "/snaps/customLog";
    private static final String CUSTOM_LOG_FILE_NAME = "/customLog.dat";

    private static volatile SnapsLogger gInstance = null;

    private Map<SnapsLoggerAttribute.LOG_TYPE, SnapsLoggerBase> loggers = null;

    private long appLaunchedTime = 0l;

    private WebLogSendExecutor webLogSendExecutor = null;

    private LinkedList<String> menuClickUrlHistory = null;

    private Map<WebLogConstants.eWebLogName, Long> sendWebLogHistories = null;

    private SnapsLogger() {
        init();
    }

    private void init() {
        loggers = new HashMap<>();
        appLaunchedTime = System.currentTimeMillis();
        webLogSendExecutor = new WebLogSendExecutor();
        menuClickUrlHistory = new LinkedList<String>();
        sendWebLogHistories = new HashMap<>();
    }

    public static void createInstance() {
        if (gInstance == null) {
            synchronized (SnapsLogger.class) {
                gInstance = new SnapsLogger();
            }
        }
    }

    public static SnapsLogger getInstance() {
        if (gInstance == null)
            createInstance();
        return gInstance;
    }

    public static void finalizeInstance() {
        if (gInstance != null) {
            if (gInstance.webLogSendExecutor != null) {
                gInstance.webLogSendExecutor = null;
            }

            gInstance = null;
        }
    }

    /**
     * 2020.04.02 Snaps로 로그 보내는것 중단.
     *
     * @param requestBuilder
     */
    public static void sendWebLog(WebLogRequestBuilder requestBuilder) {
//        if (requestBuilder == null) return;
//
//        SnapsLogger snapsLogger = getInstance();
//        if (snapsLogger.isConsecutiveSameLogEvent(requestBuilder)) {
//            return;
//        }
//
//        try {
//            WebLogRequestInfo requestInfo = WebLogRequestFactory.createURIWithLogName(requestBuilder.getLogURI());
//            if (requestInfo == null) {
//                SnapsAssert.assertNotNull(null);
//                return;
//            }
//
//            Map<WebLogConstants.eWebLogPayloadType, String> payloadMap = requestBuilder.getPayloadMap();
//            if (payloadMap != null && !payloadMap.isEmpty()) {
//                for (Map.Entry<WebLogConstants.eWebLogPayloadType, String> entry : payloadMap.entrySet()) {
//                    if (entry == null) continue;
//                    requestInfo.putPayload(entry.getKey(), entry.getValue());
//                }
//            }
//
//            sendWebLog(requestInfo);
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
    }

    private boolean isConsecutiveSameLogEvent(WebLogRequestBuilder requestBuilder) {
        if (requestBuilder == null) return false;

        WebLogConstants.eWebLogName logName = requestBuilder.getLogURI();
        if (logName == null) return false;

        if (sendWebLogHistories == null) sendWebLogHistories = new HashMap<>();

        if (sendWebLogHistories.containsKey(logName)) {
            long prevSendTime = sendWebLogHistories.get(logName);
            sendWebLogHistories.put(logName, System.currentTimeMillis());
            return System.currentTimeMillis() - prevSendTime < ALLOW_TIME_OF_CONSECUTIVE_LOG_EVENT_TRANSFER;
        } else {
            sendWebLogHistories.put(logName, System.currentTimeMillis());
        }

        return false;
    }

    private static void sendWebLog(WebLogRequestInfo requestInfo) {
        if (requestInfo == null || !requestInfo.shouldSendLogMessage()) return;
        SnapsLogger logger = getInstance();
        logger.sendWebLogByExecutor(requestInfo);
    }

    private void sendWebLogByExecutor(WebLogRequestInfo requestInfo) {
        if (webLogSendExecutor == null) webLogSendExecutor = new WebLogSendExecutor();
        webLogSendExecutor.start(requestInfo);
    }

    /**
     * 주문 진행 관련 작업 진행 중 체크하기 위한 로그 추가
     */
    public static void appendOrderLog(String log) {
        appendOrderLog(log, false);
    }

    public static void appendOrderLog(String log, boolean isInitialize) {
        try {
            appendCrashlyticsLog("orderLog", log);
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.ORDER).setContents(log).setInitializeLog(isInitialize).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 주문 진행 관련 작업 진행 중 로그를 서버로 전송한다.
     */
    public static void sendLogOrderException(@NonNull SnapsOrderConstants.eSnapsOrderType orderType, String log) {
        try {
            String detailMsg = "Order Fail msg : " + log + "\n" + SnapsLogger.getLogString(SnapsLoggerAttribute.LOG_TYPE.ORDER);
            appendLogWithLogAttribute(orderType, detailMsg);
            sendLogExceptionWithLogType("order Exception/" + orderType.toString(), SnapsLoggerAttribute.LOG_TYPE.ORDER);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 단순하게 텍스트 로그를 쌓는다. (서버로 발송하진 않음..바로 발송하려면 sendTextLog를 쓴다)
     */
    public static void appendTextLog(String log) {
        appendTextLog(log, false);
    }

    public static void appendTextLog(String key, String value) {
        appendTextLog(String.format("%s -> %s", key, value), false);
    }

    private static void appendTextLog(String log, boolean isInitialize) {
        try {
            appendCrashlyticsLog("appendTextLog", log);
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.TEXT).setContents(log).setInitializeLog(isInitialize).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 스냅스 내부에서 웹과 통신하기 위한 URL 스키마에 대한 로그
     */
    public static void appendSnapsSchemeUrlLog(String url) {
        try {
            Dlog.d("appendSnapsSchemeUrlLog() url:" + url);
            appendCrashlyticsLog("appendSnapsSchemeUrlLog", url);
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.SNAPS_SCHEME_URL).setContents(url).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 웹 - 앱 인터페이스 URL 로그
     */
    public static void appendInterfaceUrlLog(String url) {
        try {
            String logContents = String.format("\n%s", url);
            appendCrashlyticsLog("appendInterfaceUrlLog", url);
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.INTERFACE).setContents(logContents).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void initInterfaceUrlLog() {
        try {
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.INTERFACE).setContents("").setInitializeLog(true).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void sendInterfaceResultLog(String where, String result) {
        try {
            String logContents = String.format("\n>> Interface result : %s\n", result);
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.INTERFACE).setContents(logContents).create());
            sendLogExceptionWithLogType(where, SnapsLoggerAttribute.LOG_TYPE.INTERFACE);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 액티비티 이동을 추적하기 위한 로그
     */
    public static void appendClassTrackingLog(SnapsLoggerClass<?> t) {
        try {
            appendCrashlyticsLog("appendClassTrackingLog", t.getClassName());
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.CLASS_TRACKING).setContents(t.getClassName()).create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 기록되어 있는 로그의 내용을 반환한다
     */
    public static String getLogString(SnapsLoggerAttribute.LOG_TYPE logType) {
        try {
            SnapsLogger logger = getInstance();
            Map<SnapsLoggerAttribute.LOG_TYPE, SnapsLoggerBase> loggers = logger.loggers;
            SnapsLoggerBase log = loggers.get(logType);
            if (log != null)
                return ("############ " + logType.toString() + " ############\n" + log.getLog());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    /**
     * 모든 로드 내용을 반환한다
     */
    public static String getAllLogString() {
        try {
            SnapsLogger logger = getInstance();
            Map<SnapsLoggerAttribute.LOG_TYPE, SnapsLoggerBase> loggers = logger.loggers;

            StringBuilder result = new StringBuilder();
            for (SnapsLoggerAttribute.LOG_TYPE logType : loggers.keySet())
                result.append(getLogString(logType)).append("\n==========================================\n");
            return result.toString();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    public static void sendLogException(String where, Exception exception) {
        if (exception == null) return;

        try {
            if (exception instanceof SnapsLoggerBase) {
                sendLogException(where, (SnapsLoggerBase) exception);
            } else {
                sendExceptionLogWithLog(where, exception.toString());
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static String getExceptionStrAndCurrentThreadTrace(String exceptionToStr) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(exceptionToStr).append(")");
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        if (traceElements != null && traceElements.length > 0) {
            for (StackTraceElement element : traceElements) {
                if (element != null)
                    builder.append("\ncode line : ").append(element.getLineNumber()).append("___").append(element.getMethodName()).append("*").append(element.getClassName()).append("!!!");
            }
        }
        return builder.toString();
    }

    /**
     * 로그 발송
     */
    public static void sendTextLog(String where, String log) {
        if (StringUtil.isEmpty(log)) return;
        appendTextLog(log);
        sendLogExceptionWithLogType(where, SnapsLoggerAttribute.LOG_TYPE.TEXT);
    }

    /**
     * 디버깅용..
     */
    public static void sendDebugLog() {
        appendCrashlyticsLog("sendDebugLog", "debug");
//        Crashlytics.logException(SnapsDebugLoggerCreator.createLogger(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.DEBUG).setContents("debug").create()));
        sendInterfaceResultLog("debug", "result??");
//        Crashlytics.getInstance().crash();
        throw new RuntimeException("Crashlytics test crash !");
    }

    private static void sendLogException(String where, SnapsLoggerBase exception) {
        try {
            Context context = ContextUtil.getContext();
            if (context != null && !CNetStatus.getInstance().isAliveNetwork(context)) {
//                Crashlytics.logException(SnapsNetworkExceptionCreator.createExceptionWithMessage("is not alive network."));
                FirebaseCrashlytics.getInstance().recordException(SnapsNetworkExceptionCreator.createExceptionWithMessage("is not alive network."));
                return;
            }

            appendCrashlyticsLog("sendLogException", (exception != null ? exception.getLog() : ""));

            if (!StringUtil.isEmpty(where))
                Config.setOrderExceptionDesc(where);

            exception = convertDetailExceptionLogger(exception);
            FirebaseCrashlytics.getInstance().recordException(exception);
//            Crashlytics.logException(exception);

//            initLoggerHistory();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static SnapsLoggerBase convertDetailExceptionLogger(SnapsLoggerBase exception) {
        if (exception == null || exception.getLoggerAttribute() == null) return exception;

        SnapsLoggerAttribute loggerAttribute = exception.getLoggerAttribute();
        if (loggerAttribute.getLogType() == null) return exception;

        switch (loggerAttribute.getLogType()) {
            case INTERFACE: {
                loggerAttribute.setContents(exception.getLog());
                return SnapsInterfaceLoggerFactory.createInterfaceDetailException(loggerAttribute);
            }
        }

        return exception;
    }

    private static void initLoggerHistory() {
        if (getInstance() == null || getInstance().loggers == null) return;
        getInstance().loggers.clear();
    }

    private static void sendLogExceptionWithLogType(String where, SnapsLoggerAttribute.LOG_TYPE logType) {
        try {
            SnapsLogger logger = getInstance();
            Map<SnapsLoggerAttribute.LOG_TYPE, SnapsLoggerBase> loggers = logger.loggers;
            SnapsLoggerBase log = loggers.get(logType);
            if (log != null) sendLogException(where, log);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static void appendLogWithLogAttribute(@NonNull SnapsLoggerAttribute attribute) {
        try {
            SnapsLoggerBase loggerBase;

            SnapsLogger logger = getInstance();

            loggerBase = logger.loggers.get(attribute.getLogType());
            if (loggerBase == null) {
                loggerBase = SnapsLoggerFactory.createLoggerWithLoggerAttribute(attribute);
                if (loggerBase == null)
                    return;
                logger.loggers.put(attribute.getLogType(), loggerBase);
            }

            loggerBase.appendLog(attribute);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static void appendLogWithLogAttribute(SnapsOrderConstants.eSnapsOrderType orderType, String log) {
        try {
            SnapsLoggerBase loggerBase = SnapsOrderExceptionFactory.createSnapsOrderExceptionWithDetailMsg(orderType, log);
            if (loggerBase == null) {
                return;
            }

            SnapsLogger logger = getInstance();
            logger.loggers.put(SnapsLoggerAttribute.LOG_TYPE.ORDER, loggerBase);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static File writeLogToCustomLogFile(Context context, String log) {
        File customLogFile = null;
        try {
            customLogFile = new File(getCustomLogFileFullPath(context));

            if (customLogFile.exists()) {
                deletePrevCustomLogFile();
            }

            createCustomLogFile(context);

            BufferedWriter bfw = new BufferedWriter(new FileWriter(customLogFile, true));
            bfw.write(log);
            bfw.write("\n");
            bfw.flush();
            bfw.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return customLogFile;
    }

    public static File writeStandardInfoToCustomLogFile(Context context) throws Exception {
        return writeLogToCustomLogFile(context, getStandardLogInfo());
    }

    private static String getAppUseTime() throws Exception {
        long useTime = System.currentTimeMillis() - getInstance().appLaunchedTime;
        return DateUtil.getDurationBreakdown(useTime);
    }

    public static String getStandardLogInfo() throws Exception {
        Context context = ContextUtil.getContext();
        String userId = context != null ? Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_ID) : "unknown";
        String chnCode = Config.getCHANNEL_CODE();
        StringBuilder builder = new StringBuilder();
        builder.append("\nproduct make date :").append(DateUtil.getTodayFullDate()).append("\n");
        builder.append("app use time : ").append(getAppUseTime()).append("\n");
        builder.append("exception where : ").append(Config.getOrderExceptionDesc()).append("\n");
        builder.append("userId : ").append(userId).append("\n");
        builder.append("prod code : ").append(Config.getPROD_CODE()).append("\n");
        builder.append("paper code : ").append(Config.getPAPER_CODE()).append("\n");
        builder.append("tmpl code : ").append(Config.getTMPL_CODE()).append("\n");
        builder.append("glossy : ").append(Config.getGLOSSY_TYPE()).append("\n");
        builder.append("channel code : ").append(chnCode).append("\n");
        builder.append("App version : ").append(SystemUtil.getAppVersion(ContextUtil.getContext())).append("\n");
        builder.append("Total Storage space(MB) : ").append(SystemUtil.getStorageSpaceMB()).append("\n");
        builder.append("Inner Storage space(MB) : ").append(SystemUtil.getInternalMemorySizeMB()).append("\n");
        builder.append("Android OS : ").append(Build.VERSION.SDK_INT).append("\n");
        builder.append("Device Board : ").append(Build.BOARD).append("\n");
        builder.append("Device Brand : ").append(Build.BRAND).append("\n");
        builder.append("Device Device : ").append(Build.DEVICE).append("\n");
        builder.append("Device Model : ").append(Build.MODEL).append("\n");
        builder.append("is From Cart : ").append(Config.isFromCart());

        if (Build.VERSION.SDK_INT >= 23) {
            if (context != null) {
                builder.append("WRITE_EXTERNAL_STORAGE permission : ").append(context
                        .checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED).append("\n");
                builder.append("READ_EXTERNAL_STORAGE permission : ").append(context
                        .checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED).append("\n");
            }
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) ContextUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / 0x100000L;
        builder.append("Memory free : ").append(availableMegs).append("\n");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            double percentAvail = mi.availMem / (double) mi.totalMem;
            builder.append("Memory free percent : ").append(percentAvail).append("\n");
        }

        builder.append("===================================\n");
        builder.append(getAllLogString());

        return builder.toString();
    }

    private static void deletePrevCustomLogFile() throws Exception {
        File prevCustomInfoFile = new File(getCustomLogFileFullPath(ContextUtil.getContext()));
        if (prevCustomInfoFile.exists()) {
            prevCustomInfoFile.delete();
        }
    }

    private static String getCustomLogFilePath(Context context) {
        return Config.getExternalCacheDir(context) + CUSTOM_LOG_FILE_PATH;
    }

    private static String getCustomLogFileFullPath(Context context) {
        return Config.getExternalCacheDir(context) + CUSTOM_LOG_FILE_PATH + CUSTOM_LOG_FILE_NAME;
    }

    private static File createCustomLogFile(@NonNull Context context) {
        File customLogFile = null;
        try {
            customLogFile = new File(getCustomLogFilePath(context));
            if (!customLogFile.exists()) {
                customLogFile.mkdirs();
            }

            customLogFile = new File(getCustomLogFileFullPath(context));
            if (!customLogFile.exists()) {
                customLogFile.createNewFile();
                customLogFile.setWritable(true);
                customLogFile.setReadable(true);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return customLogFile;
    }

    public static void sendExceptionLogWithLog(String where, String log) {
        try {
            appendLogWithLogAttribute(new SnapsLoggerAttribute.Builder().setLogType(SnapsLoggerAttribute.LOG_TYPE.EXCEPTION).setContents(getExceptionStrAndCurrentThreadTrace(log)).create());
            sendLogExceptionWithLogType(where, SnapsLoggerAttribute.LOG_TYPE.EXCEPTION);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static void appendCrashlyticsLog(String tag, String log) {
        try {
            if ((Config.isRealServer() || Config.isDevelopVersion()) && ContextUtil.getSubContext() != null) //HOME
//                Crashlytics.log(0, tag, log);
                FirebaseCrashlytics.getInstance().log(log);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void saveMenuClickHistoryBySnapsLogger(String url) {
        if (menuClickUrlHistory == null) menuClickUrlHistory = new LinkedList<>();

        try {
            if (menuClickUrlHistory.size() > MAX_SAVE_MENU_CLICK_HISTORY_COUNT) {
                menuClickUrlHistory.poll();
            }

            menuClickUrlHistory.add(url);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public String getLastClickedMenuUrl() {
        if (menuClickUrlHistory == null || menuClickUrlHistory.isEmpty()) return "";
        return menuClickUrlHistory.peekLast();
    }
}
