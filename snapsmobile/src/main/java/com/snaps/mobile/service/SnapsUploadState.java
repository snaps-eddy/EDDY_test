package com.snaps.mobile.service;

/***
 * 업로드 상태를 Acticity와 데이터를 주고 받기위해 만든 클래스...
 * 
 * @author ifunbae
 * 
 */
public class SnapsUploadState {

	/***
	 * 업로드 상태..
	 * 
	 * @author ifunbae
	 * 
	 */
	public static enum UploadState {
		UPLOAD_READY, // 업로드 준비 - 작업이 없는경우...
		UPLOAD_START, // 업로드 시작
		UPLOADING, // 업로드
		UPLOAD_END, // 업로드 끝
		UPLOAD_ERROR, // 업로드 에러
		UPLOAD_XML_ERROR, // 업로드 XML 오류
        UploadState, UPLOAD_UNKNOW_ERROR // 알수없는 에러
	}

	private static SnapsUploadState mInstance = null;
	// 업로드 상태 값
	UploadState mState;
	// 업로드 진행중인 갯수
	int mUploadingPhotoCount = 0;
	// 업로드 완료된 갯수..
	int mCompletePhotosCount = 0;

	synchronized static public SnapsUploadState getInstance() {
		if (mInstance == null) {
			mInstance = new SnapsUploadState();
			mInstance.mState = UploadState.UPLOAD_READY;
		}

		return mInstance;
	}

	public void setmState(UploadState mState) {
		this.mState = mState;
	}

	public void setState(UploadState mState, int totalCnt, int completeCnt) {
		this.mState = mState;

		this.mUploadingPhotoCount = mState == UploadState.UPLOAD_END ? 0 : totalCnt;
		this.mCompletePhotosCount = mState == UploadState.UPLOAD_END ? 0 : completeCnt;
	}

	public void setState(int totalCnt, int completeCnt) {
		this.mUploadingPhotoCount = mState == UploadState.UPLOAD_END ? 0 : totalCnt;
		this.mCompletePhotosCount = mState == UploadState.UPLOAD_END ? 0 : completeCnt;
	}

	public UploadState getmState() {
		return mState;
	}

	// 전체 워크 카운트..
	public int getmUploadingPhotoCount() {
		return mUploadingPhotoCount;
	}

	// 완료 워크 카운트...
	public int getmCompletePhotosCount() {
		return mCompletePhotosCount;
	}

}
