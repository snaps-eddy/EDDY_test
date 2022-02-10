package com.snaps.mobile.activity.selectimage;

import android.view.View;

/***
 * 복잡한 사진선택 Activity 분기 처리 인터페이스..
 * 
 * @author hansang-ug
 *
 */
public interface ImpImageSelectActivity {

	/***
	 * 사진선택 최대 갯수 설정..
	 * 
	 * @return
	 */
	public int setMaxcount();

	/***
	 * 버튼 클릭 (다음,완료,)
	 * 
	 * @param v
	 */
	public void clickButtons(View v);

	/***
	 * 최초 템플릿 코드 및 화면 설정..
	 */
	public void init();
}


/*
 
 사진선택을 제품군을 나눠서 처리를 한다.
 1.사진인화 군
 2.sns 군
 3.심플,테마북 
 4.액자군
 5.달력 군.
 

*/