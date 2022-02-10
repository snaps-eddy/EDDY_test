package com.snaps.mobile.activity.ui.menu;

public interface IMainMenuLoadListener {
	
	public static final int LOAD_TYPE_PRICE_INFO = 0;
	public static final int LOAD_TYPE_MENU_INFO = 1;
	
	public void onMenuLoadResult(int loadType, boolean isSuccess);
}
