package errorhandle;

import android.content.Context;
import android.os.Build;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsNetworkExceptionCreator;

import errorhandle.logger.SnapsLogger;

public class LogUtil {

//	static public void sendCrashlyticsClassNameLog(String className) {
//		Crashlytics.logException(new Throwable(className));
//	}

	static public void sendCrashlyticsLog(Context context, String logMessage, String logName) {
		if (!Config.isRealServer()) return;

		String F_DEVICE_NO = Setting.getString(context, Const_VALUE.KEY_GCM_REGID);// 기기번호(device_token)
		String F_DEVICE_TYPE = Build.MODEL;// 기기TYPE
		String F_OTHER_ID = SystemUtil.getDeviceId(context);// 기기별 회원번호(uuid)
		String userId = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_ID);
		String F_USER_NO = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);// 사용자번호
		String F_OS_VER = Build.VERSION.RELEASE;// OS버전
		String F_APP_VER = SystemUtil.getAppVersion(context);// 앱버전

		StringBuffer msg = new StringBuffer();
		msg.append("message =");
		msg.append(logMessage);
		msg.append("\n");
		msg.append("device =");
		msg.append(F_DEVICE_NO);
		msg.append("\n");
		msg.append("device type =");
		msg.append(F_DEVICE_TYPE);
		msg.append("\n");
		msg.append("otherid =");
		msg.append(F_OTHER_ID);
		msg.append("\n");
		msg.append("userId =");
		msg.append(userId);
		msg.append("\n");
		msg.append("userno =");
		msg.append(F_USER_NO);
		msg.append("\n");
		msg.append("osver =");
		msg.append(F_OS_VER);
		msg.append("\n");
		msg.append("appver =");
		msg.append(F_APP_VER);

//		if (Config.isRealServer() && ContextUtil.getSubContext() != null) //HOME
//			Crashlytics.log(msg.toString());
		SnapsLogger.sendLogException("loadMenuNotWork", SnapsNetworkExceptionCreator.createExceptionWithMessage(logName));
	}
}
