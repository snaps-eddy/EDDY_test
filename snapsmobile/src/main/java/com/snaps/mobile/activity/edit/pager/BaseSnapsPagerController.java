package com.snaps.mobile.activity.edit.pager;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

import com.snaps.common.structure.page.SnapsPage;

public abstract class BaseSnapsPagerController  {
	
	public void close() {};
	
	public int getPagerSelected() {
		return 0;
	}
	
	public void loadPage(ArrayList<SnapsPage> pageList, ArrayList<Fragment> canvasList, int t, int b, int m) {}
	
	public void loadPage(ArrayList<SnapsPage> pageList, ArrayList<Fragment> canvasList, int t, int b, int m, boolean isLandScapeView) {}
}
