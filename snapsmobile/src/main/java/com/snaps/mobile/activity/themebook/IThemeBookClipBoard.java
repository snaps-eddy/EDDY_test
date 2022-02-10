package com.snaps.mobile.activity.themebook;

import com.snaps.common.structure.page.SnapsPage;

public interface IThemeBookClipBoard {
	public void copy(SnapsPage p, boolean isPaste);
	public SnapsPage getCopiedPage();
	
	public int getSelectedPageIndex();
	public void setSelectedPageIndex(int index);
	
	public void deleteClipBoardPage();
	public void setExistCopiedPage(boolean isCopied);
	public boolean isExistCopiedPage();
}
