package com.snaps.mobile.order;

import com.snaps.common.structure.SnapsTemplate;

public interface ISnapsOrderActionListener {
	void performUpload();
	void showResult(boolean isSuccess, boolean isUploadNShare, SnapsTemplate template);
}
