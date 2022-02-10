package errorhandle;

import com.snaps.mobile.activity.home.HomeActivity;
import com.snaps.mobile.activity.home.RenewalHomeActivity;

public class CatchSyncker {
	private static CatchSyncker gInstance = null;

	private HomeActivity mainAct = null;
	private RenewalHomeActivity renewalMainAct = null;

	private boolean isSendErrLog = false;

	private CatchSyncker() {
		this.isSendErrLog = false;
	}

	public boolean isSendErrLog() {
		return isSendErrLog;
	}

	public void setSendErrLog(boolean isSendErrLog) {
		this.isSendErrLog = isSendErrLog;
	}

	public static void createInstance() {
		if (gInstance == null) {
			gInstance = new CatchSyncker();
		}
	}

	public synchronized static CatchSyncker getInstance() {
		if (gInstance == null) {
			createInstance();
		}
		return gInstance;
	}

	public void setMainActivity(HomeActivity homeActivity) {
		this.mainAct = homeActivity;
	}

	public void setMainActivity(RenewalHomeActivity homeActivity) {
		this.renewalMainAct = homeActivity;
	}

	public void forceFinishApp() {
		if (gInstance != null) {
			if (gInstance.mainAct != null) {
				SnapsAssert.assertException(gInstance.mainAct, new Exception(""));
				gInstance.mainAct.forceAppFinish();
			}

			if (gInstance.renewalMainAct != null) {
				SnapsAssert.assertException(gInstance.renewalMainAct, new Exception(""));
				gInstance.renewalMainAct.forceAppFinish();
			}

			finalizeInstance();
		}
	}

	public static void finalizeInstance() {
		if (gInstance != null) {
			gInstance.setSendErrLog(false);
			gInstance = null;
		}
	}
}
