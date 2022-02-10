package com.snaps.mobile.activity.themebook;

public interface ImpDiaryUpload {

	/***
	 * 일기 시퀀스 요청
	 */
	void process1_getDiarySequence();

	/***
	 * 검증..
	 */
	void process2_checkMissionValid();

	/***
	 * 원본 이미지 저장
	 */
	void process3_orgImageUpload();
	/***
	 * 썸네일 저장요청
	 */
	void process4_pageThumnailUpload();
	/***
	 * 일기 정보 저장
	 */
	void process5_projectFileUpload();
	
}
