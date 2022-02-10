package com.snaps.common.utils.log;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.snaps.common.utils.constant.Config;

public class Dlog {
    public static final String CS_TOOL_TOY = "CS_TOOL_TOY";
    public static final String UI_MACRO = "SNAPS_UI_MACRO";
    public static final String PRE_FIX_CS = "_CS_";
    public static final String PRE_FIX_FONT = "_FONT_";

    private static boolean isEnable() {
        return Config.isDEBUG_LOGG();
    }

    public static void v(String tag, String msg) {
        if (isEnable() == false) return;

        Log.v(tag, msg);
    }

    public static void v(String msg) {
        if (isEnable() == false) return;

        Log.d(linkTag(), msg);
    }

    public static void d(String tag, String msg) {
        if (isEnable() == false) return;

        Log.d(tag, msg);
    }

    public static void d(String msg) {
        if (isEnable() == false) return;

        Log.d(linkTag(), msg);
    }

    public static void d(Object o) {
        if (isEnable() == false) return;

        Log.d(linkTag(), o.toString());
    }

    public static void i(String tag, String msg) {
        if (isEnable() == false) return;

        Log.i(tag, msg);
    }

    public static void i(String msg) {
        if (isEnable() == false) return;

        Log.d(linkTag(), msg);
    }

    public static void w(String tag, String msg) {
        writeCrashlyticsLog(0, tag, msg);

        if (isEnable() == false) return;

        Log.w(tag, msg);
    }

    public static void w(String tag, Throwable tr) {
        writeCrashlyticsLog(0, tag, tr);

        if (isEnable() == false) return;

        Log.w(tag, Log.getStackTraceString(tr));
    }

    public static void w(String tag, String msg, Throwable tr) {
        writeCrashlyticsLog(0, tag, msg, tr);

        if (isEnable() == false) return;

        Log.w(tag, msg + "\n" + Log.getStackTraceString(tr));
    }

    public static void e(String msg) {
        if (isEnable() == false) return;

        Log.e(linkTag(), msg);
    }

    public static void e(Throwable throwable) {
        if (isEnable() == false) return;

        Log.e(linkTag(), throwable.getMessage(), throwable);
    }

    public static void e(String tag, String msg) {
        writeCrashlyticsLog(0, tag, msg);

        if (isEnable() == false) return;

        Log.e(tag, msg);
    }

    public static void e(String tag, Throwable tr) {
        writeCrashlyticsLog(0, tag, tr);

        if (isEnable() == false) return;

        Log.e(tag, Log.getStackTraceString(tr));
    }

    public static void e(String tag, String msg, Throwable tr) {
        writeCrashlyticsLog(0, tag, msg, tr);

        if (isEnable() == false) return;

        Log.e(tag, msg + "\n" + Log.getStackTraceString(tr));
    }

    public static void crashlytics(String tag, String msg) {
        writeCrashlyticsLog(0, tag, msg);
    }

    private static void writeCrashlyticsLog(int priority, String tag, String msg) {
//        try {
//            Crashlytics.log(priority, tag, msg);
//        }catch (Throwable e) {
//        }
        FirebaseCrashlytics.getInstance().log(msg);
    }

    private static void writeCrashlyticsLog(int priority, String tag, Throwable tr) {
//        try {
//            Crashlytics.log(priority, tag, Log.getStackTraceString(tr));
//        }catch (Throwable e) {
//        }
        FirebaseCrashlytics.getInstance().log(Log.getStackTraceString(tr));
    }

    private static void writeCrashlyticsLog(int priority, String tag, String msg, Throwable tr) {
//        try {
//            Crashlytics.log(priority, tag, msg + "\n" + Log.getStackTraceString(tr));
//        }catch (Throwable e) {
//        }
        FirebaseCrashlytics.getInstance().log(msg + "\n" + Log.getStackTraceString(tr));
    }

    private static String linkTag() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        String fileName = trace.getFileName();
        String classPath = trace.getClassName();
        String className = classPath.substring(classPath.lastIndexOf(".") + 1);
//        String methodName = trace.getMethodName();
        int lineNumber = trace.getLineNumber();
        String linkString = "(" + fileName + ":" + lineNumber + ")";
//        String pathString = "SNAPS# " + className + "." + methodName; // Method 이름까지는 필요없을 듯 하다.
        String pathString = "SNAPS# " + className;
        if (pathString.length() + linkString.length() > 80) {
            return pathString.substring(0, 80 - linkString.length()) + "..." + linkString;
        } else {
            return pathString + linkString;
        }
    }

    /*
    public static void crashlytics(String msg) {
        appendCrashlyticsLog(msg);
    }

    public static void crashlytics(Throwable tr) {
        appendCrashlyticsLog(Log.getStackTraceString(tr));
    }

    public static void crashlytics(String msg, Throwable tr) {
        appendCrashlyticsLog(msg + "\n" + Log.getStackTraceString(tr));
    }

    private static void appendCrashlyticsLog(String msg) {
        if (Config.isRealServer() == false) return;

        try {
            Crashlytics.log(msg);
        }catch (Exception e) {
        }
    }
    */
}
