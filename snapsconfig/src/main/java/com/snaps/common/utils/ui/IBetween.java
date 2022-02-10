package com.snaps.common.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;

import com.snaps.common.data.between.BWPhotoMainResponse;
import com.snaps.common.data.between.BWProfileMainResponse;
import com.snaps.common.data.between.IBetweenAPIResultListener;

public abstract class IBetween {
	
	public static enum LOGO_TYPE {
		BACKGROUND,
		TITLE,
		ICON
	}
	
	public abstract void requestBetweenUserProfile(Context context, boolean showProgress, IBetweenAPIResultListener lis);

	public abstract void getBetweenUserProfile(Context context, BWProfileMainResponse userProfile);
	
	public abstract void requestBetweenUserPhotoList(Context context, boolean showProgress, String params, IBetweenAPIResultListener lis);
	
	public abstract void getBetweenUserPhotoList(Context context, BWPhotoMainResponse userPhotos);
	
	public abstract void getBetweenUserFirstPhotoList(Context context, BWPhotoMainResponse userPhotos);
	
	public abstract void startBetweenLoginActivity(Context context);
	
	public abstract void startActForResultBetweenLoginActivity(Context context, int requestCode);
	
	public abstract String getLastID(Context context);
	
	public abstract Bitmap getTitleLogoDrawble(LOGO_TYPE type);
	
	public abstract void setTitleLogoDrawble(LOGO_TYPE Type, Bitmap drawable);
	
	public abstract Integer[] getEventPopupResources();
	
//	public  abstract 	boolean isBetweenLogin() ;
//	public  abstract  	void    onBetweenClickLogout() ;
//	public  abstract    void 	initBetweenLoginButton();
//	
//	public abstract String 		getLastID();
//	
//	public abstract	 void sendBetweenData(Context context);
// 
//
//	public abstract void startBetweenLoginActivity(Context context);
//	
//	public abstract String createBetweenInstance(Context context);
//	
//	public abstract void assignMessageBuilder();
//	
//	public abstract void initializeSession(Context context);
	
	
//	public abstract  void getRequestBetween(String lastid, final int photoCount, final ArrayAdapter<BetweenImageData> adapter, final CustomFragment _customFragment) ;
	
	/***
	 * 
	 * @param context
	 * @param text
	 * @param url
	 * @param urlText
	 */
//	public abstract void sendInviteMessage(Context context, String text, String url, String urlText);
}
