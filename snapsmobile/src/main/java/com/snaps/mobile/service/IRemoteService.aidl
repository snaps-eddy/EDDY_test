package com.snaps.mobile.service;
import com.snaps.mobile.service.IRemoteServiceCallback;
import com.snaps.common.structure.photoprint.SnapsPhotoPrintProject;

interface IRemoteService {
	boolean registerCallback(IRemoteServiceCallback callback);
	boolean unregisterCallback(IRemoteServiceCallback callback);
	//다시시도, 취소 등등...
	boolean requestUploadProcess(int kind);
	//업로드 정보 요청... 
	boolean requestUploadProgressInfo();
	boolean addProject(in SnapsPhotoPrintProject p);
}