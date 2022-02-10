package com.snaps.common.utils.ui;

import android.content.Context;

import com.snaps.common.data.interfaces.ISnapsApplication;

public class ContextUtil {

    private static volatile ContextUtil instance;

    private Context context;
    private static Context subContext; //HomeActivity

    public static ContextUtil getInstance() {
        return instance;
    }

    public static void createInstance(ISnapsApplication snapsApplication) {
        if (instance == null) {
            synchronized (ContextUtil.class) {
                if (instance == null) {
                    instance = new ContextUtil(snapsApplication);
                }
            }
        }
    }

    /**
     * Hot fix : Rx bug 인데 디펜던시 설정이 꼬여서 여기다가 선언. 나중에 바꿔야 한다.
     * @param snapsApplication
     */
    private ContextUtil(ISnapsApplication snapsApplication) {
        this.context = snapsApplication.getSnapsApplication();
//        RxJavaPlugins.setErrorHandler(e -> {
//            if (e instanceof UndeliverableException) {
//                e = e.getCause();
//            }
//            if ((e instanceof IOException) || (e instanceof SocketException)) {
//                return;
//            }
//            if (e instanceof InterruptedException) {
//                return;
//            }
//            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
//                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
//                return;
//            }
//            if (e instanceof IllegalStateException) {
//                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
//                return;
//            }
//            Dlog.e("RxJava_HOOK", "Undeliverable exception received, not sure what to do" + e.getMessage());
//        });
    }

    public static Context getContext() {
        ContextUtil contextUtil = getInstance();
        return contextUtil != null && contextUtil.context != null ? contextUtil.context : getSubContext();
    }

    public static String getString(int id, String defaultStr) {
        return getContext() != null ? getContext().getString(id) : defaultStr;
    }

    public static void setSubContext(Context context) {
        subContext = context;
    }

    public static Context getSubContext() {
        return subContext;
    }

    public static void finalizeInstance() {
        subContext = null;
        if (getInstance() != null) {
            getInstance().context = null;
        }
    }
}