package com.snaps.mobile.component;

import android.webkit.WebView;

/***
 * 
 * @author ifunbae
 * 
 */
public interface ImpWebViewProcess {
	
	public boolean getCheckProcess();

	/***
	 * 
	 * @param view
	 * @param url
	 * @param urlData
	 * @return
	 */
	public boolean shouldOverrideUrlLoading(WebView view, String url);

	
}
