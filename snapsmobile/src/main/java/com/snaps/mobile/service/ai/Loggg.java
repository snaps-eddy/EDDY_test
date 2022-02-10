package com.snaps.mobile.service.ai;

import android.util.Log;

import com.snaps.mobile.BuildConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * 스냅스 소스를 보면 Logg 클래스가 있다.
 * 만약 그걸 사용해서 로그를 찍게 되면 스냅스 원래 로그와 같이 출력되므로 난장판이 된다.
 * 특히 서비스는 UI가 없기 때문에 로그를 이용해서 상태를 확인해야 하므로 로그 출력양이 많을 수 밖에 없다.
 * 그래서 별도의 TAG를 설정해서 logcat 메시지 분리
 */
class Loggg {
    private static final String AI_SYNC_TAG = "#AISYNC";
    private static final boolean IS_ENABLE_LOG = true;  //필요에 따라서 ON/OFF
    public static final boolean IS_DEBUG = BuildConfig.DEBUG;
    private static List<Listener> mListenerList = new ArrayList<Listener>();

    public interface Listener {
        void onWrite(int level, String msg);
    }

    public static void addListener(Listener listener) {
        synchronized (mListenerList) {
            if (mListenerList.contains(listener) == false) {
                mListenerList.add(listener);
            }
        }
    }

    public static void removeListener(Listener listener) {
        synchronized (mListenerList) {
            if (mListenerList.contains(listener)) {
                mListenerList.remove(listener);
            }
        }
    }

    private static void callListener(int level, String msg) {
        synchronized (mListenerList) {
            for (Listener listener : mListenerList) {
                listener.onWrite(level, msg);
            }
        }
    }

    public static void v(String tag, String msg) {
        if (mListenerList.size() > 0) {
            String log = tag + " | " + msg;
            callListener(Log.VERBOSE, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        String log = tag + " | " + msg;
        Log.v(AI_SYNC_TAG, log);
    }

    public static void d(String tag, String msg) {
        if (mListenerList.size() > 0) {
            String log = tag + " | " + msg;
            callListener(Log.DEBUG, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        String log = tag + " | " + msg;
        Log.d(AI_SYNC_TAG, log);
    }

    public static void i(String tag, String msg) {
        if (mListenerList.size() > 0) {
            String log = tag + " | " + msg;
            callListener(Log.INFO, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        String log = tag + " | " + msg;
        Log.i(AI_SYNC_TAG, log);
    }

    public static void w(String tag, String msg) {
        if (mListenerList.size() > 0) {
            String log = tag + " | " + msg;
            callListener(Log.WARN, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        String log = tag + " | " + msg;
        Log.w(AI_SYNC_TAG, log);
    }

    public static void w(String tag, Throwable tr) {
        if (mListenerList.size() > 0) {
            String log = tag + " | " + Log.getStackTraceString(tr);
            callListener(Log.WARN, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        String log = tag + " | " + Log.getStackTraceString(tr);
        Log.w(AI_SYNC_TAG, log);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (mListenerList.size() > 0) {
            String log = tag + " | " + msg + "\n";
            log += tag + " | " + Log.getStackTraceString(tr);
            callListener(Log.WARN, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        String log = tag + " | " + msg + "\n";
        log += tag + " | " + Log.getStackTraceString(tr);
        Log.w(AI_SYNC_TAG, log);
    }

    public static void e(String tag, String msg) {
        String log = tag + " | " + msg;

        Monitoring.getInstance().addError(log);

        if (mListenerList.size() > 0) {
            callListener(Log.ERROR, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        Log.e(AI_SYNC_TAG, log);
    }

    public static void e(String tag, Throwable tr) {
        String log = tag + " | " + Log.getStackTraceString(tr);

        Monitoring.getInstance().addError(log);

        if (mListenerList.size() > 0) {
            callListener(Log.ERROR, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        Log.e(AI_SYNC_TAG, log);
    }

    public static void e(String tag, String msg, Throwable tr) {
        String log = tag + " | " + msg + "\n";
        log += tag + " | " + Log.getStackTraceString(tr);

        Monitoring.getInstance().addError(log);

        if (mListenerList.size() > 0) {
            callListener(Log.ERROR, log);
        }

        if (IS_DEBUG == false) return;
        if (IS_ENABLE_LOG == false) return;

        Log.e(AI_SYNC_TAG, log);
    }
}
