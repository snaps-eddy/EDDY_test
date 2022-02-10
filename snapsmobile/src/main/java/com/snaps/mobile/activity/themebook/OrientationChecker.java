package com.snaps.mobile.activity.themebook;

import com.snaps.common.utils.ui.UIUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

public class OrientationChecker {
	
	public OrientationChecker(Context context) {
		this.context = context;
	}
	
	private Context context = null;
	private int prevOrientation = Configuration.ORIENTATION_PORTRAIT;
	private int orientPrevInEditor = Configuration.ORIENTATION_PORTRAIT;
	private boolean isChangedOrientationAtImgEditor = false; //사진 편집 화면에서 화면의 orientation이 변경 된 경우
	private boolean isChangedPhoto = false;
	public int getPrevOrientation() {
		return prevOrientation;
	}
	
	public void setPrevOrientation(int prevOrientation) {
		this.prevOrientation = prevOrientation;
	}
	public int getOrientPrevInEditor() {
		return orientPrevInEditor;
	}
	public void setOrientPrevInEditor(int orientPrevInEditor) {
		this.orientPrevInEditor = orientPrevInEditor;
	}
	public void setCurrentOrientationPrevInEditor() {
		if(context == null || !(context instanceof Activity)) return;
		this.orientPrevInEditor = UIUtil.getScreenOrientation((Activity) context);
	}
	public boolean isChangedOrientationAtImgEditor() {
		return isChangedOrientationAtImgEditor;
	}
	public void setChangedOrientationAtImgEditor(
			boolean isChangedOrientationAtImgEditor) {
		this.isChangedOrientationAtImgEditor = isChangedOrientationAtImgEditor;
	}
	public boolean isChangedPhoto() {
		return isChangedPhoto;
	}
	public void setChangedPhoto(boolean isChangedPhoto) {
		this.isChangedPhoto = isChangedPhoto;
	} 
	public boolean checkChangedOrientationAtImgEditor() {
		if(context == null || !(context instanceof Activity)) return false;
		int orientCurrent = UIUtil.getScreenOrientation((Activity) context);
		setChangedOrientationAtImgEditor(orientCurrent != getOrientPrevInEditor());
		return isChangedOrientationAtImgEditor();
	}
}
