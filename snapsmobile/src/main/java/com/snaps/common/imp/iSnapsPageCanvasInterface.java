package com.snaps.common.imp;


public interface iSnapsPageCanvasInterface {
	
	/**
	 * Canvas 이미지 로딩 시작
	 */
	public void onImageLoadStart();
	
	
	/**
	 * 
	 * Canvas 이미지 로드 완료.
	 * 
	 * @param page
	 *            현재 페이지.
	 */
	public void onImageLoadComplete(int page);

}
