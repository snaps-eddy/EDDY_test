package com.snaps.mobile.interfaces;

/***
 * 뷰 하단에 있는 업데이트 뷰 인터페이스
 * 
 * @author ifunbae
 * 
 */
public interface ImpUploadStateView {
	// 프로그래스바, 아이콘
	// 텍스트, 진행텍스트
	// 버튼 체인지..

	/***
	 * 전체 뷰를 보일지 말지.. 설정..
	 * 
	 * @param isVisible
	 */
	void setVisible(int visible);

	/***
	 * 프로그래스바를 보이는 함수...
	 * 
	 * @param isShow
	 */
	void showProgressbar(int visible);

	/***
	 * 아이콘을 설정하는 함수..
	 * 
	 * @param resid
	 */
	void showIcon(int resid);

	/***
	 * 정보 테스트를 설정하는 함수 예:사진 업로드 중...
	 * 
	 * @param text
	 */
	void setInfoText(String text);

	/***
	 * 업로드 진행 상태텍스틑 뿌리는 함수
	 * 
	 * @param current
	 * @param total
	 */
	void setProgressStateText(float current, float total);

	/***
	 * 버튼 텍스트를 설정하는 함수..
	 * 
	 * @param btnText
	 */
	void setButtonText(String btnText);
}
