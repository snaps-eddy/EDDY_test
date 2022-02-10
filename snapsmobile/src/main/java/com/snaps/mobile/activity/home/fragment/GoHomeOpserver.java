package com.snaps.mobile.activity.home.fragment;

import android.content.Context;

import com.snaps.common.push.PushManager;

import java.util.HashSet;
import java.util.Set;

public abstract class GoHomeOpserver {

    public interface OnGoHomeOpserver {
        void onGoHome();
    }

    private static Set<OnGoHomeOpserver> m_setActiveActitivies = new HashSet<OnGoHomeOpserver>();

    public static void addGoHomeListener(OnGoHomeOpserver op) {
        m_setActiveActitivies.add(op);
    }

    public static void removeGoHomeListenrer(OnGoHomeOpserver op) {
        if (m_setActiveActitivies != null && m_setActiveActitivies.contains(op)) {
            m_setActiveActitivies.remove(op);
        }
    }

    public static void removeAllGoHomeListenrer() {
        if (m_setActiveActitivies == null) return;
        m_setActiveActitivies.clear();
    }

    public static void notifyGoHome() {
        notifyGoHome(null);
    }

    public static void notifyGoHome(final Context context) {
        notifyGoHome(context, false);
    }

    public static void notifyGoHome(final Context context, boolean isLogOut) {
        if (isLogOut) {
            if (context != null) {
//                final String regId = Setting.getString(context, Const_VALUE.KEY_GCM_REGID);
//                if (!"".equalsIgnoreCase(regId)) {
//                    AsyncTask.execute(() -> HttpReq.regPushDevice(
//                            Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE) ? regId : "",
//                            Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO),
//                            Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NAME),
//                            SystemUtil.getAppVersion(context),
//                            SystemUtil.getIMEI(context),
//                            SnapsInterfaceLogDefaultHandler.createDefaultHandler()));
//                }
                PushManager service = new PushManager(context);
                service.requestRegistPushDevice();
            }
        }

        for (OnGoHomeOpserver op : m_setActiveActitivies) {
            if (op != null)
                op.onGoHome();
        }
    }
}
