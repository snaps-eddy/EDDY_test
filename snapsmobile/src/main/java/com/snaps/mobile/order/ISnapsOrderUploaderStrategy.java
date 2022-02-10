package com.snaps.mobile.order;


public interface ISnapsOrderUploaderStrategy {
	
	void upload();
	
	boolean isUploading();

	void setOrderUploadCallback(ISnapsOrderUploadBridgeRequest callback);
}
