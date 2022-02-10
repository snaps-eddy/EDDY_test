package com.snaps.mobile.order.order_v2.datas;

import com.snaps.mobile.order.ISnapsOrderStateListener;

/**
 * Created by ysjeong on 2017. 4. 19..
 */

public class SnapsOrderState {

	public static SnapsOrderState newInstance() {
		return new SnapsOrderState();
	}

	public static String PAUSE_CAPTURE = "pause_capture";
	public static String PAUSE_UPLOAD_COMPLETE = "pause_upload_complete";
	public static String PAUSE_APPLICATION = "pause_application";
	public static String PAUSE_IMGSAVE = "pause_imgsave";
	public static String IMAGE_EDITING = "img_editing";

	private ISnapsOrderStateListener snapsOrderStateListener;

	private String pauseStateCode = "";

	private boolean isUploadingProject = false;

	public String getPauseStateCode() {
		return pauseStateCode;
	}

	public void setPauseStateCode(String pauseStateCode) {
		this.pauseStateCode = pauseStateCode;
	}

	public boolean isUploadingProject() {
		return isUploadingProject;
	}

	public void setUploadingProject(boolean uploadingProject) {
		isUploadingProject = uploadingProject;
	}

	public ISnapsOrderStateListener getSnapsOrderStateListener() {
		return snapsOrderStateListener;
	}

	public void setSnapsOrderStateListener(ISnapsOrderStateListener snapsOrderStateListener) {
		this.snapsOrderStateListener = snapsOrderStateListener;
	}
}
