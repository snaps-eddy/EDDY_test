package errorhandle.logger;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.FileLogAsynTask;
import com.snaps.common.utils.ui.ContextUtil;

public class Logg {
	/*
	public static void d(String msg) {
		if (Config.isDEBUG_LOGG()) {
			l(msg);
		}
		if (Config.isFILE_LOGG()) {
			FileLogAsynTask task = new FileLogAsynTask();
			task.execute(new String[]{msg});
		}

		appendCrashlyticsLog(msg);
	}

	public static void d(String tag, String msg) {
		if (Config.isDEBUG_LOGG()) {
			l(tag + "-" + msg);
		}

		if (Config.isFILE_LOGG()) {
			FileLogAsynTask task = new FileLogAsynTask();
			task.execute(new String[]{tag, msg});

		}

		appendCrashlyticsLog(msg);
	}

	public static void l(String tag, String msg) {
		Log.d("ROK", msg == null ? "null" : tag + "-" + msg);
	}

	public static void l(String msg) {
		Log.d("ROK", msg == null ? "null" : msg);
	}

	public static void y(String msg) {
		if (Config.isDEBUG_LOGG()) {
			Log.w("#TEST", msg);
		}

		appendCrashlyticsLog(msg);
	}

	public static void y(String msg, Context con) {
		if (Config.isDEBUG_LOGG()) {
			Log.w("#TEST", msg);
			Toast.makeText(con, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public static void y(String tag, String msg) {
		if (Config.isDEBUG_LOGG()) {
			Log.w("#TEST", tag + " : " + msg);
		}
	}

	public static void y(String tag, float msg) {
		if (Config.isDEBUG_LOGG()) {
			Log.w("#TEST", tag + " : " + msg);
		}
	}

	public static void y(String tag, int msg) {
		if (Config.isDEBUG_LOGG()) {
			Log.w("#TEST", tag + " : " + msg);
		}
	}

	private static void appendCrashlyticsLog(String log) {
		try {
			if (Config.isRealServer() && ContextUtil.getSubContext() != null) //HOME
			{
				Crashlytics.log(log);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}
