package com.snaps.mobile.utils.smart_snaps;

import android.os.Looper;
import android.os.Message;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsAnimationListener;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.log.Dlog;

import java.util.LinkedList;

/**
 * Created by ysjeong on 2018. 1. 17..
 */

public class SmartSnapsAnimationHandler implements ISnapsHandler {
	private static final String TAG = SmartSnapsAnimationHandler.class.getSimpleName();
	private SmartSnapsAnimationListener smartSnapsAnimationListener = null;
	private SnapsHandler snapsHandler = null;
	private LinkedList<SmartSnapsAnimationHandleLooper> loopers = null;
	private SmartSnapsConstants.eSmartSnapsProgressType progressType = SmartSnapsConstants.eSmartSnapsProgressType.NONE;
	private SmartSnapsAnimationHandleLooper smartSnapsAnimationHandleLooper = null;
	private boolean isSuspended = false;

	public static SmartSnapsAnimationHandler createSmartSnapsAnimationHandler(SmartSnapsAnimationListener smartSnapsAnimationListener, SmartSnapsConstants.eSmartSnapsProgressType progressType) {
		return new SmartSnapsAnimationHandler(smartSnapsAnimationListener, progressType);
	}

	public void startSmartSnapsAutoFitImage(int startPageIndex) throws Exception {
		SmartSnapsManager.setSmartAreaSearching(true);
		if (snapsHandler != null) {
			snapsHandler.sendEmptyMessage(HANDLE_MSG_START); //Progress 처리를 하는 시점
		}

		handleSmartSnapsAnimationOnPage(startPageIndex);
	}

	public void suspendTasks() {
		isSuspended = true;
		if (loopers != null) {
			while (!loopers.isEmpty()) {
				SmartSnapsAnimationHandleLooper looper = loopers.poll();
				if (looper == null) {
					continue;
				}
				looper.notifyUploadReadyImageDataSyncLocker();
				looper.setSuspend();
			}
		}
	}

	private SmartSnapsAnimationHandler(SmartSnapsAnimationListener smartSnapsAnimationListener, SmartSnapsConstants.eSmartSnapsProgressType progressType) {
		this.smartSnapsAnimationListener = smartSnapsAnimationListener;
		this.snapsHandler = new SnapsHandler(Looper.getMainLooper(), this);
		this.loopers = new LinkedList<>();
		this.isSuspended = false;
		this.progressType = progressType;
	}

	public void handleSmartSnapsAnimationOnPage(int pageIndex) {
		if (!SmartSnapsManager.isSmartAreaSearching() || isSuspended) {
			return;
		}

		smartSnapsAnimationHandleLooper = SmartSnapsAnimationHandleLooper.createNewLooperWithPageIndex(pageIndex, snapsHandler);
		smartSnapsAnimationHandleLooper.start();

		if (loopers != null) {
			loopers.add(smartSnapsAnimationHandleLooper);
		}
	}

	public SmartSnapsAnimationHandleLooper getCurrentSmartSnapsAnimationHandleLooper() {
		return smartSnapsAnimationHandleLooper;
	}

	public void removeUploadReadyImageData(MyPhotoSelectImageData imageData) {
		try {
			if (getCurrentSmartSnapsAnimationHandleLooper() != null) {
				getCurrentSmartSnapsAnimationHandleLooper().removeUploadReadyImageData(imageData);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public SmartSnapsConstants.eSmartSnapsProgressType getProgressType() {
		return progressType;
	}

	public void initProgressType() {
		this.progressType = SmartSnapsConstants.eSmartSnapsProgressType.NONE;
	}

	private static final int HANDLE_MSG_START = 0;
	static final int HANDLE_MSG_FINISH = 1;
	static final int HANDLE_MSG_SWIPE = 2;
	static final int HANDLE_MSG_REFRESH_THUMBNAIL = 3;
	static final int HANDLE_MSG_ANIMATION = 4;
	static final int HANDLE_MSG_INCREASE_PROGRESS = 5;
	static final int HANDLE_MSG_EXCEPTION = 1000;

	@Override
	public void handleMessage(Message msg) {
		if (isSuspended) {
			return;
		}

		switch (msg.what) {
			case HANDLE_MSG_START:
				if (smartSnapsAnimationListener != null) {
					smartSnapsAnimationListener.onSmartSnapsAnimationStart(progressType);
				}
				break;
			case HANDLE_MSG_SWIPE:
				if (smartSnapsAnimationListener != null) {
					smartSnapsAnimationListener.requestSmartAnimationWithPage(msg.arg1);
				}
				break;
			case HANDLE_MSG_FINISH:
				if (smartSnapsAnimationListener != null) {
					smartSnapsAnimationListener.onSmartSnapsAnimationFinish(progressType);
				}
				break;
			case HANDLE_MSG_REFRESH_THUMBNAIL:
				if (smartSnapsAnimationListener != null) {
					smartSnapsAnimationListener.requestRefreshPageThumbnail(msg.arg1);
				}
				break;
			case HANDLE_MSG_ANIMATION:
				if (smartSnapsAnimationListener != null) {
					smartSnapsAnimationListener.requestAnimation((MyPhotoSelectImageData) msg.obj);
				}
				break;
			case HANDLE_MSG_EXCEPTION:
				if (smartSnapsAnimationListener != null) {
					smartSnapsAnimationListener.onOccurredException((Exception) msg.obj);
				}
				break;
			case HANDLE_MSG_INCREASE_PROGRESS:
				if (smartSnapsAnimationListener != null) {
					SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
					smartSnapsManager.increaseSmartSnapsFinishTaskCount();
					smartSnapsAnimationListener.onSmartSnapsAnimationUpdateProgress(progressType, smartSnapsManager.getSmartSnapsTaskTotalCount(), smartSnapsManager.getSmartSnapsFinishTaskCount());
				}
				break;
		}
	}
}
