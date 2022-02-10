package com.snaps.mobile.activity.themebook;

/***
 * 프로젝트 업로더.. 구현..
 * 포토북 업로더
 * 사진인화류 업로더... 구현..
 * 
 * @author hansang-ug
 *
 */
public interface ImpProjectUpload {

	/***
	 * 프로젝트 코드를 생성한다.
	 */
	void process1_getProjectCode();
	
	/**
	 * 주문 번호의 유효성 검사.(이미 주문 완료 된 상품이 아닌지.) 
	 */
	void verifyProjectCode(String projectCode, boolean isEditMode);

	/**
	 * 페이지 썸네일을 생성한다.
	 */
	void makePagesThumbnail();
	
	/***
	 * 프로젝트 대표 썸네일을 업로드 한다.
	 */
	void process2_thumbnailUpload();
	/***
	 * 프로젝트 페이지 이미지를 업로드 한다.
	 */
	void process3_pageThumnailUpload();
	/***
	 * 원본 이미지를 업로드 한다.
	 */
	void process4_orgImageUpload();
	
	void process4_orgImageUpload2();
	/***
	 * 프로젝트 파일을 업로드 한다. auraOrder.xml,save.xml,prjOption.xml
	 */
	void process5_projectFileUpload();
}
