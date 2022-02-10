package com.snaps.mobile.component.image_edit_componet;

/**
 * Created by ysjeong on 2017. 6. 8..
 */

public class SnapsImageCropImageRectInfo {
	private ImgRectAttribute curImgRect = new ImgRectAttribute(); //현재 이미지를 컨트롤 하기 위함.
	private ImgRectAttribute totalImgRect = new ImgRectAttribute(); //모든 이동 궤적이나 스케일이 합산된 상태
	private ImgRectAttribute touchdownImgRect = new ImgRectAttribute(); //터치 다운한 상태를 저장.
	private ImgRectAttribute lastAllowImgRect = new ImgRectAttribute(); //터치 다운한 상태를 저장.

	public ImgRectAttribute getCurImgRect() {
		return curImgRect;
	}

	public void setCurImgRect(ImgRectAttribute curImgRect) {
		this.curImgRect = curImgRect;
	}

	public ImgRectAttribute getTotalImgRect() {
		return totalImgRect;
	}

	public void setTotalImgRect(ImgRectAttribute totalImgRect) {
		this.totalImgRect = totalImgRect;
	}

	public ImgRectAttribute getTouchdownImgRect() {
		return touchdownImgRect;
	}

	public void setTouchdownImgRect(ImgRectAttribute touchdownImgRect) {
		this.touchdownImgRect = touchdownImgRect;
	}

	public ImgRectAttribute getLastAllowImgRect() {
		return lastAllowImgRect;
	}

	public void setLastAllowImgRect(ImgRectAttribute lastAllowImgRect) {
		this.lastAllowImgRect = lastAllowImgRect;
	}
}
