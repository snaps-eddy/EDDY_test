package com.snaps.mobile.utils.ui;

/***
 * 카카오 좌표를 px로 변화 해주는 함수...
 * mm => px
 * 
 * @author hansang-ug
 *
 */
public class CalcMMToPX {
	float PXPERMM = 1.580f; // 카카오 px / mm

	public CalcMMToPX(float pxPerMM) {
		PXPERMM = pxPerMM;
	}

	/***
	 * mm 을 px로 변화 해준다..
	 * 
	 * @param mm
	 * @return
	 */
	public int calcMM(float mm) {
		return (int) Math.round(mm * PXPERMM);
	}
}
