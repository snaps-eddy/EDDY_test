package errorhandle;

import android.content.Context;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpReq.ISendErrMsgListener;
import com.snaps.common.utils.ui.ICrashlytics;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class CatchExceptionHandler implements Thread.UncaughtExceptionHandler {
	private static final String TAG = CatchExceptionHandler.class.getSimpleName();
	
	UncaughtExceptionHandler defaultUEH;
	
	ISendErrMsgListener sendErrMsgListener;
	
	ICrashlytics creashlyticListener;
	
	Context context;

	private void createCrashlyticsHandler() {
		if (!Config.isRealServer()) return;

		@SuppressWarnings("rawtypes")
		Class klass;
		try {
			klass = Class.forName("com.snaps.mobile.order.order_v2.exceptions.CrashlyticHandler");
			creashlyticListener = (ICrashlytics) klass.newInstance();
		} catch (ClassNotFoundException e) {
			Dlog.e(TAG, e);
		} catch (InstantiationException e) {
			Dlog.e(TAG, e);
		} catch (IllegalAccessException e) {
			Dlog.e(TAG, e);
		}
	}
	
	public CatchExceptionHandler(Context context) {
		defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		
		this.context = context;

		if(context != null) {
			if(!SnapsTPAppManager.isThirdPartyApp(context))
				createCrashlyticsHandler();
		}
	}

	@Override
	public synchronized void uncaughtException(Thread thread, Throwable ex) {
		// 잡을 수 없는 에러 출력여부
		if (Config.isDEBUG_LOGG())
			Dlog.e(TAG, ex);

		//자동 저장 된 내용을 복구하다가 죽었다면, 답이 없다. 파일을 지워야 한다.
		AutoSaveManager saveMan = AutoSaveManager.getInstance();
		if(saveMan != null && saveMan.isRecoveryMode()) {
			saveMan.finishAutoSaveMode();
		}
		
		if(!Config.isRealServer(context)) return;

		if(creashlyticListener == null || creashlyticListener.isAppFinishedByCrash()) return;
		creashlyticListener.setAppFinishedByCrash();

		if(creashlyticListener != null)
			creashlyticListener.postThrowable(ex);
		
//		String F_PROD_CODE = Config.getPROD_CODE();//상품코드
//		String F_APP_TYPE = context.getPackageName();//어플리케이션타입
//		String F_HPPN_TYPE = "";//발생경로
//		String F_DEVICE_NO = Setting.getString(context, Const_VALUE.KEY_GCM_REGID);//기기번호(device_token)
//		String F_DEVICE_TYPE = Build.MODEL;//기기TYPE
//		String F_OTHER_ID = SystemUtil.getIMEI(context);//기기별 회원번호(uuid)
//
//		String kakaoId = Setting.getString(context, Const_VALUE.KEY_KAKAOTALK_USER_ID);
//		String userId = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_ID);
//
//		String F_UUSER_ID = !"".equals(kakaoId) ? kakaoId : userId; //협력사회원ID
//		String F_USER_NO = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);//사용자번호
//		String F_ERR_MSG = getStackTrace(ex);//에러메시지
//		String F_OS_TYPE = Const_VALUE.GCM_ANDROID_OS_TYPE;//OS타입
//		String F_OS_VER = Build.VERSION.RELEASE;//OS버전
//		String F_APP_VER = SystemUtil.getAppVersion(context);//앱버전
//		String F_ERR_CLASS = Config.getCurrentClassName();//에러클래스
//
//		F_ERR_MSG += SnapsLogger.getLogString(SnapsLoggerAttribute.LOG_TYPE.CLASS_TRACKING);

		sendErrMsgListener = new ISendErrMsgListener() {
			@Override
			public void onPosted() {
				//비정상 종료 처리 되면 앱을 그냥 종료 시켜 버림.
				if(creashlyticListener != null) {
					creashlyticListener.forceAppFinish();
				}
			}
		};
		
//		HttpReq.sendErrorLog(F_PROD_CODE, F_APP_TYPE, F_HPPN_TYPE, F_DEVICE_NO, F_DEVICE_TYPE, F_OTHER_ID, F_UUSER_ID, F_USER_NO, F_ERR_MSG, F_OS_TYPE, F_OS_VER, F_APP_VER, F_ERR_CLASS, sendErrMsgListener);
		defaultUEH.uncaughtException(thread, ex);// os용 error handler에서 처리
	}

	String getStackTrace(Throwable th) {
		String stacktraceAsString = "";
		PrintWriter printWriter = null;
		try {
			Writer result = new StringWriter();
			printWriter = new PrintWriter(result);
			Throwable cause = th;
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			stacktraceAsString = result.toString();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			if (printWriter != null)
				printWriter.close();
		}
		return stacktraceAsString;
	}
}
