package com.snaps.common.utils.ui;
/*
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.data.img.MyPhotoSelectImageData;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class IFacebook {

	public abstract void init(Activity act);
//	public abstract    IFacebook createInstance();
	public abstract boolean isFacebookLogin();
	
	public interface OnFBComplete {
		public void onFBComplete(String result);
	}

	public interface OnPaging {
		public void onPagingComplete(JSONObject jsonObj);
	}
	
	public abstract void facebookLoginChk(Activity act, OnFBComplete onComp) ;
	public abstract void addCallback();
	public abstract void removeCallback();
	public abstract void saveInstance(Bundle bundle);
	public abstract void onActivityResult(Activity act,int requestCode, int resultCode, Intent data);
	public abstract void facebookLogout();
	public abstract boolean facebookGetPhotos(Activity act, String nextKey, int paging, final OnPaging onPaging);

//	public abstract boolean facebookGetPhotos(Activity act, int idx, int paging, final OnPaging onPaging, int width, int height) ;
	
	public abstract void facebookGetPostInfosForBatch(ArrayList<MyPhotoSelectImageData> returnList) ;
	
	public abstract void setContext(Context context);

	public abstract void activeApp(Context context, String string);
	
		
	
	

}
