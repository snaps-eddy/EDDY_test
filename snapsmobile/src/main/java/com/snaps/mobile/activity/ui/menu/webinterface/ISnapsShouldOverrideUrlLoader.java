package com.snaps.mobile.activity.ui.menu.webinterface;

import android.webkit.WebView;

public interface ISnapsShouldOverrideUrlLoader {

	public boolean shouldOverrideUrlLoading(WebView view, String url);
	
	public boolean shouldOverrideUrlLoading(String url);
}
