package com.snaps.common.model;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.ui.menu.renewal.model.Menu;

import java.util.ArrayList;

import errorhandle.logger.Logg;

public class WebViewPage {
	private static final String TAG = WebViewPage.class.getSimpleName();
	public String title;
	public String url;
	public boolean isBadgeExist = false;
    public boolean isHomePage = false;
	public String data = "";
	public ArrayList<Menu> menuList = null;

	public WebViewPage( String title, String url ) {
		this.title = title;
		this.url = url;
		this.isBadgeExist = false;
		Dlog.d("WebViewPage() title:" + title + ", url:" + url);
	}
	
	public WebViewPage( String title, String url, boolean isBadgeExist, boolean isHomePage ) {
		this.title = title;
		this.url = url;
		this.isBadgeExist = isBadgeExist;
        this.isHomePage = isHomePage;
		Dlog.d("WebViewPage() title:" + title + ", url:" + url);
	}

    public WebViewPage( String title, String url, boolean isBadgeExist ) {
        this.title = title;
        this.url = url;
        this.isBadgeExist = isBadgeExist;

		Dlog.d("WebViewPage() title:" + title + ", url:" + url);
	}
	
	public void setMenuList(ArrayList<Menu> list) {
		this.menuList = list;
	}

}
